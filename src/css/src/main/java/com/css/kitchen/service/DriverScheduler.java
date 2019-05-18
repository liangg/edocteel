package com.css.kitchen.service;

import com.css.kitchen.Kitchen;
import com.css.kitchen.common.Order;
import com.css.kitchen.util.MetricsManager;

import java.util.Optional;
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

  public DriverScheduler(Kitchen kitchen) { this.kitchen = kitchen; }

  @Override
  public String name() { return "DriverScheduler"; }

  private final Runnable task = new Runnable() {
    @Override
    public void run() {
      // pick an order from the kitchen
      Optional<Order> orderOptional = kitchen.pickup();
      if (orderOptional.isPresent()) {
        logger.debug("driver pickup order: " + orderOptional.get());
        MetricsManager.incr(MetricsManager.DELIVERED_ORDERS);
      }
    }
  };

  public void start() {
    logger.info("DriverScheduler starts");
  }

  public void scheduleDriverPickup() {
    // FIXME: pick a random delay 2-10
    // a driver typically take 2 to 10 seconds to arrive for order pickup
    int driveTime = 1;
    executor.schedule(task, driveTime, TimeUnit.SECONDS);
  }
}
