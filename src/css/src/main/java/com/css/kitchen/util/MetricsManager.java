package com.css.kitchen.util;

import java.lang.Integer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manage counters for various kitchen statistics.
 */
public abstract class MetricsManager {
  public static final int RECEIVED_ORDERS  = 0;
  public static final int SUBMITTED_ORDERS = 1;
  public static final int DELIVERED_ORDERS = 2;
  public static final int WASTED_ORDERS    = 3;
  public static final int INVALID_ORDERS   = 4;

  private static AtomicInteger receivedOrders = new AtomicInteger(0);
  private static AtomicInteger submittedOrders = new AtomicInteger(0);
  private static AtomicInteger deliveredOrders = new AtomicInteger(0);
  private static AtomicInteger invalidOrders = new AtomicInteger(0);
  private static AtomicInteger wastedOrders = new AtomicInteger(0);

  public static void incr(int metricIndex) {
    switch (metricIndex) {
      case RECEIVED_ORDERS:
        receivedOrders.incrementAndGet();
        break;
      case SUBMITTED_ORDERS:
        submittedOrders.incrementAndGet();
        break;
      case DELIVERED_ORDERS:
        deliveredOrders.incrementAndGet();
        break;
      case WASTED_ORDERS:
        wastedOrders.incrementAndGet();
        break;
      case INVALID_ORDERS:
        invalidOrders.incrementAndGet();
        break;
      default:
        break;
    }
  }

  public static void report() {
    System.out.println("Kitchen business stats");
    System.out.println("======================");
    System.out.println(String.format("Number of received orders: %d", receivedOrders.get()));
    System.out.println(String.format("Number of submmited orders: %d", submittedOrders.get()));
  }
}
