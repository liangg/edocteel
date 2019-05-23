package com.css.kitchen.service;

import com.css.kitchen.Kitchen;
import com.css.kitchen.common.DriverOrder;
import com.css.kitchen.common.Order;
import com.css.kitchen.util.MetricsManager;

import java.lang.Long;
import java.lang.Math;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Driver scheduler service. It schedules a driver to pick up a food order from the
 * central kitchen and deliver to the customer.
 */
public class DriverScheduler extends CssScheduler {
  private static Logger logger = LoggerFactory.getLogger(DriverScheduler.class);

  private final Kitchen kitchen;
  private final ConcurrentLinkedDeque<DriverOrder> workOrders = new ConcurrentLinkedDeque<>();

  public DriverScheduler(Kitchen kitchen) {
    super(3);
    this.kitchen = kitchen;
  }

  @Override
  public String name() { return "DriverScheduler"; }

  private final Runnable task = new Runnable() {
    @Override
    public void run() {
      // pick up a work order id
      final DriverOrder driverOrder = workOrders.removeFirst();
      // pick an order from the kitchen
      Optional<Order> orderOptional = kitchen.pickup(driverOrder);
      if (orderOptional.isPresent()) {
        logger.debug(String.format("driver pickup order(%d): %s", driverOrder.getOrderId(), orderOptional.get()));
        MetricsManager.incr(MetricsManager.DELIVERED_ORDERS);
      } else {
        logger.debug(String.format("driver pickup missing order(%d)", driverOrder.getOrderId()));
        MetricsManager.incr(MetricsManager.MISSED_PICKUPS);
      }
    }
  };

  public void start() {
    logger.info("DriverScheduler starts");
  }

  public void scheduleDriverPickup(DriverOrder order) {
    // add order_id to driver work orders queue
    this.workOrders.add(order);
    int driveTime = estimatedArrival();
    executor.schedule(task, driveTime, TimeUnit.SECONDS);
    logger.debug(String.format("driver will arrive in %d seconds for order(%d)", driveTime, order.getOrderId()));
  }

  // A driver typically take 2 to 10 seconds to arrive for order pickup
  private static int estimatedArrival() {
    return (int)(Math.random() * 9 + 2);
  }

  public boolean hasOrder() {
    return this.workOrders.size() == 0;
  }
}
