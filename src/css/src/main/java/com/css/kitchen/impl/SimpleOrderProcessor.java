package com.css.kitchen.impl;

import static com.css.kitchen.Kitchen.HOT_SHELF;
import static com.css.kitchen.Kitchen.COLD_SHELF;
import static com.css.kitchen.Kitchen.FROZEN_SHELF;
import static com.css.kitchen.Kitchen.OVERFLOW_SHELF;

import com.css.kitchen.Order;
import com.css.kitchen.OrderProcess;
import com.css.kitchen.Shelf;
import java.lang.Override;
import java.util.ArrayList;

/**
 * An order processor that encapsulates order shelving strategy
 */
public class SimpleOrderProcessor implements OrderProcess {
    private final Shelf[] foodShelves;

    public SimpleOrderProcessor(Shelf[] foodShelves) {
        this.foodShelves = foodShelves;
    }

    @Override
    public void processOrder(Order order) {
        Shelf shelf = order.isHot() ?
                foodShelves[HOT_SHELF] :
                (order.isCold() ? foodShelves[COLD_SHELF] : foodShelves[FROZEN_SHELF]);
        if (!shelf.add(order)) {
            foodShelves[OVERFLOW_SHELF].add(order);
        }
    }
}