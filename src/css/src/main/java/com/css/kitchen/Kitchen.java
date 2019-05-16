package com.css.kitchen;

import com.css.kitchen.service.DriverScheduler;
import com.css.kitchen.service.OrderProcessor;
import com.css.kitchen.service.OrderSource;
import com.css.kitchen.util.StatsManager;

import java.lang.System;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * A CSS kitchen that takes food orders, shelf and dispatch order to drivers.
 */
public class Kitchen {
    public static int HOT_SHELF = 0;
    public static int COLD_SHELF = 1;
    public static int FROZEN_SHELF = 2;
    public static int OVERFLOW_SHELF = 3;
    public static int NUM_SHELVES = 4;

    final private Shelf[] foodShelves = new Shelf[NUM_SHELVES];
    final private OrderProcessor orderProcessor;
    final private DriverScheduler driverScheduler;

    public Kitchen() {
        // create food shelves and use simple order processor and dispatcher
        foodShelves[HOT_SHELF] = new Shelf(Shelf.Type.HotFood);
        foodShelves[COLD_SHELF] = new Shelf(Shelf.Type.ColdFood);
        foodShelves[FROZEN_SHELF] = new Shelf(Shelf.Type.FrozenFood);
        foodShelves[OVERFLOW_SHELF] = new Shelf(Shelf.Type.Overflow);
        this.orderProcessor = new OrderProcessor(this.foodShelves);
        this.driverScheduler = new DriverScheduler();
    }

    private void open() {
        System.out.println("CSS Kitchen is open");
        this.driverScheduler.start();
        this.orderProcessor.start();
    }

    private void close() {
        this.orderProcessor.shutdown();
        this.driverScheduler.shutdown();
        StatsManager.report();
        System.out.println("CSS Kitchen is closed");
    }

    public void submitOrder(Order order) {
        this.orderProcessor.submit(order);
    }

    public Optional<Order> pickup() {
        // FIXME
        return Optional.empty();
    }

    public static void main(String[] args) {
        // pass food orders json file on the command line
        if (args.length < 1) {
            System.out.println("Error: missing orders json file");
            System.exit(0);
        }
        // "/Users/liang_guo/workspace/edocteel/src/css/src/main/resources/food_orders.json"
        final String ordersJsonFile = args[0];

        Kitchen kitchen = new Kitchen();
        kitchen.open();

        // start simulated order source processor
        OrderSource sourcer = new OrderSource(kitchen);
        sourcer.start(ordersJsonFile);

        // examine whether there is incoming orders
        while (sourcer.hasOrder()) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ex) {
            }
        }

        // shutdown the application
        sourcer.shutdown();
        kitchen.close();
    }
}
