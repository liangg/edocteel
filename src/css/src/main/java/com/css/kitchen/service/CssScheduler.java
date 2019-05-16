package com.css.kitchen.service;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** The base for scheduled service */
public abstract class CssScheduler {
  private static Logger logger = LoggerFactory.getLogger(CssScheduler.class);

  protected ScheduledExecutorService executor;

  protected CssScheduler() {
    this(1);
  }

  protected CssScheduler(int numThreads) {
    this.executor = Executors.newScheduledThreadPool(numThreads);
  }

  public abstract String name();

  public void shutdown() {
    try {
      logger.info(name() + " is about to shut down");
      this.executor.shutdown();
      this.executor.awaitTermination(5, TimeUnit.SECONDS);
    }
    catch (InterruptedException e) {
      logger.error(name() + " interrupted");
    }
    finally {
      this.executor.shutdownNow();
      logger.info(name() + " shut down");
    }
  }
}
