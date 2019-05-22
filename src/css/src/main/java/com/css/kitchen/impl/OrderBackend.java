package com.css.kitchen.impl;

import com.css.kitchen.Kitchen;
import com.css.kitchen.common.DriverOrder;
import com.css.kitchen.common.Order;
import com.css.kitchen.util.MetricsManager;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;
import java.util.Optional;

/**
 * The Order service backend implements Kitchen food order fulfillment business logic.
 *
 * In production, this should be a stateful service instance that has its own partition
 * of Shelf. A multiple distributed backend instances allow better concurrency of Order
 * processing at run time.
 */
public class OrderBackend {
  private static Logger logger = LoggerFactory.getLogger(OrderBackend.class);

  public static int HOT_SHELF = 0;
  public static int COLD_SHELF = 1;
  public static int FROZEN_SHELF = 2;
  public static int OVERFLOW_SHELF = 3;
  public static int NUM_SHELVES = 4;

  // global unique order id source
  private static long orderId = 0;

  private final Kitchen kitchen;
  private final IdGenerator idGenerator = new IdGenerator();
  @Getter final private Shelf[] foodShelves;
  private final ReentrantLock lock = new ReentrantLock();

  public OrderBackend(Kitchen kitchen) {
    this(kitchen, Shelf.SHELF_SIZE, Shelf.OVERFLOW_SIZE);
  }

  public OrderBackend(Kitchen kitchen, int shelfSize, int overflowSize) {
    this.kitchen = kitchen;
    this.foodShelves = new Shelf[NUM_SHELVES];
    foodShelves[HOT_SHELF] = new Shelf(Shelf.Type.HotFood, shelfSize);
    foodShelves[COLD_SHELF] = new Shelf(Shelf.Type.ColdFood, shelfSize);
    foodShelves[FROZEN_SHELF] = new Shelf(Shelf.Type.FrozenFood, shelfSize);
    foodShelves[OVERFLOW_SHELF] = new Shelf(Shelf.Type.Overflow, overflowSize);
  }

  // Simple business logic to shelve an Order
  public void process(Order order) {
    Preconditions.checkState(order != null);
    final Shelf shelf = order.isHot() ?
        foodShelves[HOT_SHELF] :
        (order.isCold() ? foodShelves[COLD_SHELF] : foodShelves[FROZEN_SHELF]);
    final long orderId = idGenerator.nextOrderId();
    logger.debug(String.format("OrderBackend process order(%d): %s", orderId, order));
    // start 2PL for concurrency correctness
    lock.lock();
    try {
      if (!shelf.addOrder(order, orderId)) {
        logger.debug(String.format("OrderBackend process order(%d) overflow: %s ", orderId, order));
        MetricsManager.incr(MetricsManager.OVERFLOW_ORDERS);
        foodShelves[OVERFLOW_SHELF].overflow(order, orderId);
      }
    } finally {
      lock.unlock();
    }

    // schedule a driver to pick up the order
    this.kitchen.scheduleDriver(new DriverOrder(orderId, order.getType()));
  }

  public Optional<Order> pickup(DriverOrder order) {
    Order result = null;
    // start 2PL for concurrency correctness
    lock.lock();
    try {
      Shelf shelf = shelfForOrder(order.getOrderType());
      Optional<Shelf.FetchResult> fetchResult = shelf.fetchOrder(order.getOrderId());
      if (fetchResult.isPresent()) {
        result = fetchResult.get().getOrder();
        // backfill an order from Overflow shelf, if any
        if (fetchResult.get().getBackfill()) {
          Optional<ShelfOrder> backfillOptional = foodShelves[OVERFLOW_SHELF].getBackfill(result.getType(), now());
          if (backfillOptional.isPresent()) {
            shelf.backfillOrder(backfillOptional.get());
            // FIXME: check backfill result
          }
        }
      } else {
        // try look up in the Overflow shelf
        fetchResult = foodShelves[OVERFLOW_SHELF].fetchOrder(order.getOrderId());
        if (fetchResult.isPresent()) {
          result = fetchResult.get().getOrder();
        }
      }
    } finally {
      lock.unlock();
    }
    return Optional.ofNullable(result);
  }

  private Shelf shelfForOrder(Order.FoodType type) {
    return type == Order.FoodType.Hot ?
        foodShelves[HOT_SHELF] :
        (type == Order.FoodType.Cold ? foodShelves[COLD_SHELF] : foodShelves[FROZEN_SHELF]);
  }

  private static long now() {
    return DateTimeUtils.currentTimeMillis();
  }
}
