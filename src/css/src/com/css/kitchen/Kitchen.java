package com.css.kitchen;

import com.css.kitchen.impl.SimpleOrderProcessor;
import com.css.kitchen.impl.SimpleOrderDispatcher;
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

    public static int SHELF_SIZE = 15;
    public static int OVERFLOW_SIZE = 20;

    final private Shelf[] foodShelves = new Shelf[NUM_SHELVES];
    final private OrderProcessor orderProcessor;
    final private OrderDispatch orderDispatcher;

    public Kitchen() {
        foodShelves[HOT_SHELF] = new Shelf(Shelf.HotFood, SHELF_SIZE);
        foodShelves[COLD_SHELF] = new Shelf(Shelf.ColdFood, SHELF_SIZE);
        foodShelves[FROZEN_SHELF] = new Shelf(Shelf.FrozenFood, SHELF_SIZE);
        foodShelves[OVERFLOW_SHELF] = new Shelf(Shelf.Overflow, OVERFLOW_SIZE);
        this.orderProcessor = new SimpleOrderProcessor(this.foodShelves);
        this.orderDispatcher = new SimpleOrderDispatcher();
    }

    public void placeOrder(Order order) {
        orderProcessor.processOrder(order);
    }

    public Optional<Order> dispatch() {
        return Optional.ofNullable(orderDispatcher.dispatchOrder());
    }


}
