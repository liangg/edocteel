package com.css.kitchen.framework;

import com.css.kitchen.Order;
import com.css.kitchen.util.OrderReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OrderSource {
  private static Logger logger = LoggerFactory.getLogger(OrderSource.class);

  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
  private List<Order> orders;
  private int lastPosition = 0;

  public OrderSource() {
    // read orders from json to list for simulated order receiving
    ClassLoader classLoader = this.getClass().getClassLoader();
    URL ordersUrl = classLoader.getResource("food_orders.json");
    this.orders = OrderReader.readOrdersJson(ordersUrl.getFile());
  }

  public void start() {
    Runnable task = () -> {
      if (lastPosition < orders.size()) {
        Order order = orders.get(lastPosition++);
        logger.debug("submit %s", order);
        // FIXME: submit kitchen order queues
      }
    };

    logger.info("schedule order sourcing task");
    executor.schedule(task, 100, TimeUnit.MILLISECONDS);
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
}
