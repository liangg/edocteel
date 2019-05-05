package com.css.kitchen;

import com.css.kitchen.OrderProcess;
import com.css.kitchen.OrderDispatch;
import com.css.kitchen.impl.SimpleOrderProcessor;
import com.css.kitchen.impl.SimpleOrderDispatch;

import java.lang.System;
import java.util.ArrayList;
import java.util.Optional;

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
        System.out.println("CSS Kitchen");
    }
}
