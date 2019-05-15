package com.css.kitchen.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manage counters for various kitchen statistics.
 */
public class StatsManager {
  public static AtomicInteger receivedOrders = new AtomicInteger(0);
  public static AtomicInteger submittedOrders = new AtomicInteger(0);
  public static AtomicInteger deliveredOrders = new AtomicInteger(0);
  public static AtomicInteger invalidOrders = new AtomicInteger(0);
  public static AtomicInteger wastedOrders = new AtomicInteger(0);

  public static void report() {
    System.out.println("Kitchen business stats");
    System.out.println("======================");
    System.out.println(String.format("Number of received orders: %d", receivedOrders.get()));
    System.out.println(String.format("Number of submmited orders: %d", submittedOrders.get()));
  }
}
