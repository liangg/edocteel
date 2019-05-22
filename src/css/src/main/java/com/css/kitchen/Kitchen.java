package com.css.kitchen;

import com.css.kitchen.common.DriverOrder;
import com.css.kitchen.common.Order;
import com.css.kitchen.impl.IdGenerator;
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
  final private OrderBackend orderBackend;
  final private OrderSource orderSourcer;
  final private OrderProcessor orderProcessor;
  final private DriverScheduler driverScheduler;

  public Kitchen() {
    this.orderSourcer = new OrderSource(this);
    this.orderBackend = new OrderBackend(this);
    this.orderProcessor = new OrderProcessor(this);
    this.driverScheduler = new DriverScheduler(this);
  }

  private void open(String ordersJsonFile) {
    System.out.println("CSS Kitchen is open");
    // start simulated order source processor
    this.orderSourcer.start(ordersJsonFile);
    // start order delivery scheduling service
    this.driverScheduler.start();
    this.orderProcessor.start();
  }

  private void close() {
    this.orderSourcer.shutdown();
    this.orderProcessor.shutdown();
    this.driverScheduler.shutdown();
    MetricsManager.report();
    System.out.println("CSS Kitchen is closed");
  }

  private boolean isOpen() {
    return orderSourcer.hasOrder() || driverScheduler.hasOrder();
  }

  // used by OrderSource to submit new customer orders to the Kitchen
  public void submitOrder(Order order) {
    this.orderProcessor.submit(order);
  }

  // used by OrderProcessor to route an order for fulfillment
  public void fulfill(Order order) {
    this.orderBackend.process(order);
  }

  // used by OrderBackend to schedule a driver to deliver an order
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
    final String ordersJsonFile = args[0];

    Kitchen kitchen = new Kitchen();
    kitchen.open(ordersJsonFile);

    // examine whether there is incoming orders, used to coordinate kitchen close
    while (kitchen.isOpen()) {
      try {
        TimeUnit.SECONDS.sleep(5);
      } catch (InterruptedException ex) {
      }
    }

    // shutdown the application
    kitchen.close();
  }
}
