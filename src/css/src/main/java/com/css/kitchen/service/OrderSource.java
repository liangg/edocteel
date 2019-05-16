package com.css.kitchen.service;

import com.css.kitchen.Kitchen;
import com.css.kitchen.Order;
import com.css.kitchen.util.MetricsManager;
import com.css.kitchen.util.OrderReader;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor
public class OrderSource extends CssScheduler {
  private static Logger logger = LoggerFactory.getLogger(OrderSource.class);

  private Kitchen kitchen;
  private Lock lock = new ReentrantLock();
  private volatile boolean ordersExhausted = false; // used for app termination
  private List<Order> orders = Collections.emptyList();
  @Getter private int lastPosition = 0;

  public OrderSource(Kitchen kitchen) {
    this.kitchen = kitchen;
  }

  @Override
  public String name() { return "OrderSource"; }

  public void start(String orderJsonFile) {
    // read orders from json to list for simulated order receiving
    this.orders = OrderReader.readOrdersJson(orderJsonFile);

    // submit orders in simulated poisson distribution rate
    Runnable task = () -> {
      if (lastPosition < orders.size()) {
        Order order = orders.get(lastPosition++);
        MetricsManager.incr(MetricsManager.SUBMITTED_ORDERS);
        logger.debug("submit order " + order);
        // FIXME: submit kitchen order queues
        return;
      }

      // we have submitted our orders, notify kitchen to close the shop
      try {
        if (lock.tryLock(500, TimeUnit.MILLISECONDS)) {
          if (!ordersExhausted) {
            ordersExhausted = true;
          }
        }
      } catch (InterruptedException ex) {
        logger.error("order source task failed to acquire lock");
        ex.printStackTrace();
      } finally {
        lock.unlock();
      }
    };

    logger.info("OrderSource schedules task");
    executor.scheduleAtFixedRate(task, 100, 100, TimeUnit.MILLISECONDS);
  }

  public boolean hasOrder() {
    boolean result = true;
    try {
      if (lock.tryLock(200, TimeUnit.MILLISECONDS)) {
        result = ordersExhausted;
      }
    } catch (InterruptedException ex) {
      logger.error("order source check failed to acquire lock");
      ex.printStackTrace();
    } finally {
      lock.unlock();
    }
    return !result;
  }
}
