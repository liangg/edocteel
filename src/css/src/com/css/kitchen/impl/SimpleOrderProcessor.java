package com.css.kitchen.impl;

import static com.css.kitchen.Kitchen.HOT_SHELF;
import static com.css.kitchen.Kitchen.COLD_SHELF;
import static com.css.kitchen.Kitchen.FROZEN_SHELF;
import static com.css.kitchen.Kitchen.OVERFLOW_SHELF;

import java.lang.Override;

/**
 * An order processor that encapsulates order shelving strategy
 */
public class SimpleOrderProcessor implements OrderProcess {
    private final int foodShelfSize;
    private final int overflowShelfSize;
    private final ArrayList<Shelf> foodShelves;

    public OrderProcessor(ArrayList<Shelf> foodShelves, int shelfSize, inte overflowSize) {
        this.foodShelfSize = shelfSize;
        this.overflowShelfSize = overflowSize;
        this.foodShelves = foodShelves;
    }

    @Override
    public void processOrder(Order order) {
        Shelf shelf = order.isHot() ?
                foodShelves[HOT_SHELF] :
                (order.isCold() ? foodShelves[COLD_SHELF] : foodShelves[FROZEN_SHELF])
        if (!shelf.add(order)) {
            foodShelves[OVERFLOW_SHELF].add(order);
        }
    }
}