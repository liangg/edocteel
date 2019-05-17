package com.css.kitchen.service;

import static com.css.kitchen.Kitchen.HOT_SHELF;
import static com.css.kitchen.Kitchen.COLD_SHELF;
import static com.css.kitchen.Kitchen.FROZEN_SHELF;
import static com.css.kitchen.Kitchen.OVERFLOW_SHELF;

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

/**
 * An order processor that encapsulates order shelving strategy
 */
public class OrderProcessor extends CssScheduler {
  private static Logger logger = LoggerFactory.getLogger(OrderProcessor.class);
  private static int ORDER_QUEUE_SIZE = 50;

  private final Shelf[] foodShelves;
  private BlockingQueue<Order> ordersQueue;

  public OrderProcessor(Shelf[] foodShelves) {
    this(foodShelves, ORDER_QUEUE_SIZE);
  }

  public OrderProcessor(Shelf[] foodShelves, int ordersQueueSize) {
    super(1);
    this.foodShelves = foodShelves;
    ordersQueue = new LinkedBlockingQueue<>(ordersQueueSize);
  }

  @Override
  public String name() { return "OrderProcessor"; }

  private Optional<Order> fetch() {
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
      foodShelves[OVERFLOW_SHELF].add(order);
    }
  }

  public void start() {
    Runnable task = () -> {
      Optional<Order> orderOptional = fetch();
      if (!orderOptional.isPresent()) {
        return;
      }
      final Order order = orderOptional.get();
      logger.debug("process order: " + order);
      MetricsManager.incr(MetricsManager.PROCESSED_ORDERS);
      // FIXME: shelf
    };

    logger.info("OrderProcessor schedules task");
    executor.scheduleAtFixedRate(task, 25, 50, TimeUnit.MILLISECONDS);
  }
}