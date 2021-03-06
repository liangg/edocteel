package com.css.kitchen.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.css.kitchen.Kitchen;
import com.css.kitchen.common.DriverOrder;
import com.css.kitchen.common.Order;
import com.css.kitchen.impl.Shelf;
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
        .type(Order.FoodType.Hot)
        .shelfLife(600)
        .decayRate(0.45)
        .build();

    sushiOrder = Order.builder()
        .name("Sushi dragonroll")
        .type(Order.FoodType.Cold)
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
        // 3 sec is enough to take 4 orders with poisson lambda 3.25/sec
        TimeUnit.SECONDS.sleep(3);
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
    driverScheduler.scheduleDriverPickup(new DriverOrder(1L, Order.FoodType.Hot));
    driverScheduler.scheduleDriverPickup(new DriverOrder(2L, Order.FoodType.Cold));
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

