package com.css.kitchen;

import com.css.kitchen.common.DriverOrder;
import com.css.kitchen.common.Order;
import com.css.kitchen.impl.OrderBackend;
import com.css.kitchen.service.DriverScheduler;
import com.css.kitchen.service.OrderProcessor;
import com.css.kitchen.service.OrderSource;
import com.css.kitchen.util.MetricsManager;

import java.lang.System;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

/**
 * A CSS kitchen that takes food orders, process them to shelf, and dispatch orders to drivers.
 */
public class Kitchen {
  @Getter final private OrderBackend orderBackend;
  final private OrderProcessor orderProcessor;
  final private DriverScheduler driverScheduler;

  public Kitchen() {
    this.orderBackend = new OrderBackend(this);
    this.orderProcessor = new OrderProcessor(this);
    this.driverScheduler = new DriverScheduler(this);
  }

  private void open() {
    System.out.println("CSS Kitchen is open");
    this.driverScheduler.start();
    this.orderProcessor.start();
  }

  private void close() {
      this.orderProcessor.shutdown();
      this.driverScheduler.shutdown();
      MetricsManager.report();
      System.out.println("CSS Kitchen is closed");
  }

  // used by OrderSource to submit new customer orders to the kitchen
  public void submitOrder(Order order) {
    this.orderProcessor.submit(order);
  }

  public void scheduleDriver(DriverOrder order) {
    this.driverScheduler.scheduleDriverPickup(order);
  }

  // used by DriverScheduler task to pick up a ready order for delivery to hungry customers
  public Optional<Order> pickup(DriverOrder order) {
    return this.orderBackend.pickup(order);
  }

  public static void main(String[] args) {
    // pass food orders json file on the command line
    if (args.length < 1) {
      System.out.println("Error: missing orders json file");
      System.exit(0);
    }
    // "/Users/liang_guo/workspace/edocteel/src/css/src/main/resources/food_orders.json"
    final String ordersJsonFile = args[0];

    Kitchen kitchen = new Kitchen();
    kitchen.open();

    // start simulated order source processor
    OrderSource sourcer = new OrderSource(kitchen);
    sourcer.start(ordersJsonFile);

    // examine whether there is incoming orders, used to coordinate kitchen close
    while (sourcer.hasOrder()) {
      try {
        TimeUnit.SECONDS.sleep(5);
      } catch (InterruptedException ex) {
      }
    }

    // shutdown the application
    sourcer.shutdown();
    kitchen.close();
  }
}
