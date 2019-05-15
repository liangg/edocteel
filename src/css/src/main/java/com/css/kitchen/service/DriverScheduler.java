package com.css.kitchen.service;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@NoArgsConstructor
public class DriverScheduler extends CssScheduler {
  private static Logger logger = LoggerFactory.getLogger(DriverScheduler.class);

  public String name() { return "DriverScheduler"; }

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

  public void scheduleDriverPickup() {
    // FIXME: pick a random delay 2-10
    int driveTime = 1;
    executor.schedule(task, driveTime, TimeUnit.SECONDS);
  }
}
