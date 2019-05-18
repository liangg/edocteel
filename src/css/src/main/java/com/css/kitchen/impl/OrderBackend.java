package com.css.kitchen.impl;

import static com.css.kitchen.Kitchen.HOT_SHELF;
import static com.css.kitchen.Kitchen.COLD_SHELF;
import static com.css.kitchen.Kitchen.FROZEN_SHELF;
import static com.css.kitchen.Kitchen.OVERFLOW_SHELF;

import com.css.kitchen.Kitchen;
import com.css.kitchen.common.Order;
import com.css.kitchen.impl.Shelf;
import com.css.kitchen.util.MetricsManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Order backend implements Kitchen food order fullfillment business logic.
 */
public class OrderBackend {
  private static Logger logger = LoggerFactory.getLogger(OrderBackend.class);

  private final Kitchen kitchen;
  private final Shelf[] foodShelves;

  public OrderBackend(Kitchen kitchen) {
    this.kitchen = kitchen;
    this.foodShelves = kitchen.getFoodShelves();
  }

  // simple business logic to shelve an Order, in production it could be a
  // food service rpc call.
  public void processOrder(Order order) {
    final Shelf shelf = order.isHot() ?
        foodShelves[HOT_SHELF] :
        (order.isCold() ? foodShelves[COLD_SHELF] : foodShelves[FROZEN_SHELF]);
    if (!shelf.addOrder(order)) {
      foodShelves[OVERFLOW_SHELF].overflow(order);
    }
  }
}
