package com.css.kitchen.service;

import com.css.kitchen.Order;
import com.css.kitchen.util.OrderReader;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.css.kitchen.util.StatsManager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor
public class OrderSource {
  private static Logger logger = LoggerFactory.getLogger(OrderSource.class);

  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
  private List<Order> orders = Collections.emptyList();
  @Getter private int lastPosition = 0;
  private Lock lock = new ReentrantLock();
  public volatile boolean ordersExhausted = false; // used for app termination

  public void start(String orderJsonFile) {
    // read orders from json to list for simulated order receiving
    this.orders = OrderReader.readOrdersJson(orderJsonFile);

    Runnable task = () -> {
      if (lastPosition < orders.size()) {
        Order order = orders.get(lastPosition++);
        StatsManager.submittedOrders.incrementAndGet();
        logger.debug("submit order " + order);
        // FIXME: submit kitchen order queues
        return;
      }

      try {
        if (lock.tryLock(2, TimeUnit.SECONDS)) {
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

    logger.info("schedule order sourcing task");
    executor.scheduleAtFixedRate(task, 100, 100, TimeUnit.MILLISECONDS);
  }

  public void shutdown() {
    try {
      logger.info("OrderSource is to shut down");
      executor.shutdown();
      executor.awaitTermination(5, TimeUnit.SECONDS);
    }
    catch (InterruptedException e) {
      logger.error("OrderSource interrupted");
    }
    finally {
      executor.shutdownNow();
      logger.info("OrderSource shutdown");
    }
  }

  public boolean hasOrder() {
    boolean result = true;
    try {
      if (lock.tryLock(3, TimeUnit.SECONDS)) {
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
