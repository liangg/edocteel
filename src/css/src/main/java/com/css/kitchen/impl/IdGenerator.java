package com.css.kitchen.impl;

import java.util.concurrent.atomic.AtomicLong;

/**
 * It generates a globally unique order ID for each food Order. In production, it should
 * use a snowflake id or simple uuid, and it should be highly concurrent service.
 */
public class IdGenerator {
  private static AtomicLong orderIdBase = new AtomicLong(0);

  // Simple unique order ID generation, but should be snowflake or uuid.
  public static long nextOrderId() {
      return orderIdBase.incrementAndGet();
  }
}
