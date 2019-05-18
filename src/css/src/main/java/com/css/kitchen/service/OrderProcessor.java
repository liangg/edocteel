package com.css.kitchen.service;

import static com.css.kitchen.Kitchen.HOT_SHELF;
import static com.css.kitchen.Kitchen.COLD_SHELF;
import static com.css.kitchen.Kitchen.FROZEN_SHELF;
import static com.css.kitchen.Kitchen.OVERFLOW_SHELF;

import com.css.kitchen.Kitchen;
import com.css.kitchen.common.Order;
import com.css.kitchen.common.Shelf;
import com.css.kitchen.util.MetricsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Override;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.joda.time.DateTimeUtils;

/**
 * An order processor that encapsulates order shelving strategy
 */
public class OrderProcessor extends CssScheduler {
  private static Logger logger = LoggerFactory.getLogger(OrderProcessor.class);
  private static int ORDER_QUEUE_SIZE = 50;

  private final Kitchen kitchen;
  private final Shelf[] foodShelves;
  private BlockingQueue<Order> ordersQueue;

  public OrderProcessor(Kitchen kitchen) {
    this(kitchen, ORDER_QUEUE_SIZE);
  }

  public OrderProcessor(Kitchen kitchen, int ordersQueueSize) {
    super(1);
    this.kitchen = kitchen;
    this.foodShelves = kitchen.getFoodShelves();
    ordersQueue = new LinkedBlockingQueue<>(ordersQueueSize);
  }

  @Override
  public String name() { return "OrderProcessor"; }

  private Optional<Order> nextOrder() {
    Order order = null;
    try {
      order = ordersQueue.poll(50, TimeUnit.MILLISECONDS);
    } catch (InterruptedException ex) {
      logger.error("OrderProcessor queue fetch exception");
    }
    return Optional.ofNullable(order);
  }

  // submit Order to the bounded blocking queue
  public void submit(Order order) {
    try {
      ordersQueue.put(order);
    } catch (InterruptedException ex) {
      logger.error("OrderProcessor queue put exception");
      ex.printStackTrace();
    }
  }

  public void processOrder(Order order) {
    final Shelf shelf = order.isHot() ?
        foodShelves[HOT_SHELF] :
        (order.isCold() ? foodShelves[COLD_SHELF] : foodShelves[FROZEN_SHELF]);
    if (!shelf.add(order)) {
      foodShelves[OVERFLOW_SHELF].overflow(order);
    }
  }

  public void start() {
    Runnable task = () -> {
      Optional<Order> orderOptional = nextOrder();
      if (!orderOptional.isPresent()) {
        return;
      }
      final Order order = orderOptional.get();
      logger.debug("process order: " + order);
      MetricsManager.incr(MetricsManager.PROCESSED_ORDERS);
      // add the order to shelf
      processOrder(order);
      // schedule a driver to pick up the order
      this.kitchen.scheduleDriver();
    };

    logger.info("OrderProcessor schedules task");
    executor.scheduleAtFixedRate(task, 25, 50, TimeUnit.MILLISECONDS);
  }

  private static long now() {
    return DateTimeUtils.currentTimeMillis();
  }
}