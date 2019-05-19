package com.css.kitchen.impl;

import com.css.kitchen.Kitchen;
import com.css.kitchen.common.Order;
import com.css.kitchen.impl.Shelf;
import com.css.kitchen.util.MetricsManager;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Optional;

/**
 * The Order backend implements Kitchen food order fullfillment business logic.
 *
 * In production, this should be a stateful service instance that has its own partition
 * of Shelf. A mutiple distributed backend instances allow better concurrency of Order
 * processing at run time.
 */
public class OrderBackend {
  private static Logger logger = LoggerFactory.getLogger(OrderBackend.class);

  public static int HOT_SHELF = 0;
  public static int COLD_SHELF = 1;
  public static int FROZEN_SHELF = 2;
  public static int OVERFLOW_SHELF = 3;
  public static int NUM_SHELVES = 4;

  private final Kitchen kitchen;
  @Getter final private Shelf[] foodShelves;
  private final ReentrantLock lock = new ReentrantLock();

  public OrderBackend(Kitchen kitchen) {
    this.kitchen = kitchen;
    this.foodShelves = new Shelf[NUM_SHELVES];
    // create food shelves and use simple order processor and dispatcher
    foodShelves[HOT_SHELF] = new Shelf(Shelf.Type.HotFood);
    foodShelves[COLD_SHELF] = new Shelf(Shelf.Type.ColdFood);
    foodShelves[FROZEN_SHELF] = new Shelf(Shelf.Type.FrozenFood);
    foodShelves[OVERFLOW_SHELF] = new Shelf(Shelf.Type.Overflow);
  }

  // simple business logic to shelve an Order, in production it could be a
  // food service rpc call.
  public void process(Order order) {
    Preconditions.checkState(order != null);
    final Shelf shelf = order.isHot() ?
        foodShelves[HOT_SHELF] :
        (order.isCold() ? foodShelves[COLD_SHELF] : foodShelves[FROZEN_SHELF]);
    logger.debug("OrderBackend process order: " + order);
    // start 2PL for concurrency correctness
    lock.lock();
    try {
      if (!shelf.addOrder(order)) {
        logger.debug("OrderBackend process order overflow: " + order);
        MetricsManager.incr(MetricsManager.OVERFLOW_ORDERS);
        foodShelves[OVERFLOW_SHELF].overflow(order);
      }
    } finally {
      lock.unlock();
    }
  }

  public Optional<Order> pickup() {
    // start 2PL for concurrency correctness
    lock.lock();
    try {
      // FIXME
    } finally {
      lock.unlock();
    }
    return Optional.empty();
  }
}
