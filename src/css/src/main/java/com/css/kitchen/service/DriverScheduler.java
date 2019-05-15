package com.css.kitchen.service;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
public class DriverScheduler {
  private static Logger logger = LoggerFactory.getLogger(DriverScheduler.class);

  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

  private final Runnable task = new Runnable() {
    @Override
    public void run() {
      // FIXME: call Kitchen pickup
      logger.debug("driver pickup order");
    }
  };

  public void start() {
    logger.info("DriverScheduler starts");
  }

  public void shutdown() {
    try {
      logger.info("DriverScheduler is to shut down");
      executor.shutdown();
      executor.awaitTermination(5, TimeUnit.SECONDS);
    }
    catch (InterruptedException e) {
      logger.error("DriverScheduler interrupted");
    }
    finally {
      executor.shutdownNow();
      logger.info("DriverScheduler shuts down");
    }
  }

  public void scheduleDriverPickup() {
    // FIXME: pick a random delay 2-10
    int driveTime = 1;
    executor.schedule(task, driveTime, TimeUnit.SECONDS);
  }
}
