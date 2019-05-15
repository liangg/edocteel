package com.css.kitchen.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class ServiceTest {
  @Test
  public void testOrderSourceScheduledTask() {
    ClassLoader classLoader = this.getClass().getClassLoader();
    URL ordersUrl = classLoader.getResource("test_orders.json");
    OrderSource sourcer = new OrderSource();
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
    DriverScheduler driverScheduler = new DriverScheduler();
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
}
