package com.css.kitchen.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.css.kitchen.Kitchen;
import com.css.kitchen.common.Order;
import com.css.kitchen.common.Shelf;
import org.junit.Before;
import org.junit.Test;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class ServiceTest {
  private Order ramenOrder;
  private Order sushiOrder;
  private Kitchen kitchen;

  @Before
  public void init() {
    ramenOrder = Order.builder()
        .name("Ramen")
        .temperature(Order.Temperature.Hot)
        .shelfLife(600)
        .decayRate(0.45)
        .build();

    sushiOrder = Order.builder()
        .name("Sushi dragonroll")
        .temperature(Order.Temperature.Cold)
        .shelfLife(900)
        .decayRate(0.15)
        .build();

    kitchen = new Kitchen();
  }

  @Test
  public void testOrderSourceScheduledTask() {
    ClassLoader classLoader = this.getClass().getClassLoader();
    URL ordersUrl = classLoader.getResource("test_orders.json");
    OrderSource sourcer = new OrderSource(kitchen);
    sourcer.start(ordersUrl.getFile());
    while (sourcer.hasOrder()) {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException ex) {
      }
    }
    sourcer.shutdown();
    assertEquals(sourcer.getLastPosition(), 4);
  }

  @Test
  public void testDriverSchedulerTask() {
    DriverScheduler driverScheduler = new DriverScheduler(kitchen);
    driverScheduler.start();
    driverScheduler.scheduleDriverPickup();
    driverScheduler.scheduleDriverPickup();
    // wait for driver tasks to be done
    try {
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException ex) {
    }
    driverScheduler.shutdown();
  }

  @Test
  public void testOrderProcessorTask() {
    OrderProcessor orderProcessor = new OrderProcessor(kitchen);
    orderProcessor.start();
    orderProcessor.submit(ramenOrder);
    orderProcessor.submit(sushiOrder);
    try {
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException ex) {
    }
    orderProcessor.shutdown();
  }
}

