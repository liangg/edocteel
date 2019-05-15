package com.css.kitchen;

import com.css.kitchen.impl.SimpleOrderProcessor;
import com.css.kitchen.impl.SimpleOrderDispatch;
import com.css.kitchen.service.DriverScheduler;
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
    final private OrderProcess orderProcessor;
    final private OrderDispatch orderDispatcher;

    public Kitchen() {
        // create food shelves and use simple order processor and dispatcher
        foodShelves[HOT_SHELF] = new Shelf(Shelf.Type.HotFood);
        foodShelves[COLD_SHELF] = new Shelf(Shelf.Type.ColdFood);
        foodShelves[FROZEN_SHELF] = new Shelf(Shelf.Type.FrozenFood);
        foodShelves[OVERFLOW_SHELF] = new Shelf(Shelf.Type.Overflow);
        this.orderProcessor = new SimpleOrderProcessor(this.foodShelves);
        this.orderDispatcher = new SimpleOrderDispatch();
    }

    public void placeOrder(Order order) {
        orderProcessor.processOrder(order);
    }

    public Optional<Order> dispatch() {
        return Optional.ofNullable(orderDispatcher.dispatchOrder());
    }

    public static void main(String[] args) {
        // pass food orders json file on the command line
        if (args.length < 1) {
            System.out.println("Error: missing orders json file");
            System.exit(0);
        }
        // "/Users/liang_guo/workspace/edocteel/src/css/src/main/resources/food_orders.json"
        final String ordersJsonFile = args[0];

        System.out.println("CSS Kitchen is open");

        // start simulated order source processor
        OrderSource sourcer = new OrderSource();
        sourcer.start(ordersJsonFile);

        DriverScheduler driverScheduler = new DriverScheduler();
        driverScheduler.start();

        // examine whether there is incoming orders
        while (sourcer.hasOrder()) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ex) {
            }
        }

        // shutdown the application
        sourcer.shutdown();
        driverScheduler.shutdown();
        System.out.println("CSS Kitchen is closed");
        StatsManager.report();
    }
}
