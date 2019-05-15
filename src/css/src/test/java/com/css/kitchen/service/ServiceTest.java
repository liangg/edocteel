package com.css.kitchen.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class FrameworkTest {
  @Test
  public void testOrderSourceScheduledTask() {
    ClassLoader classLoader = this.getClass().getClassLoader();
    URL ordersUrl = classLoader.getResource("test_orders.json");
    OrderSource sourcer = new OrderSource();
    sourcer.start(ordersUrl.getFile());
    while (sourcer.hasOrder()) {
      try {
        TimeUnit.SECONDS.sleep(2);
      } catch (InterruptedException ex) {
      }
    }
    sourcer.shutdown();
    assertEquals(sourcer.getLastPosition(), 4);
  }
}

