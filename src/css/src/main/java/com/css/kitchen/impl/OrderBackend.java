package com.css.kitchen.impl;

import com.css.kitchen.Kitchen;
import com.css.kitchen.common.Order;
import com.css.kitchen.impl.Shelf;
import com.css.kitchen.util.MetricsManager;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The Order backend implements Kitchen food order fullfillment business logic.
 */
public class OrderBackend {
  private static Logger logger = LoggerFactory.getLogger(OrderBackend.class);

  public static int HOT_SHELF = 0;
  public static int COLD_SHELF = 1;
  public static int FROZEN_SHELF = 2;
  public static int OVERFLOW_SHELF = 3;
  public static int NUM_SHELVES = 4;

  private final Kitchen kitchen;
  @Getter final private Shelf[] foodShelves;

  public OrderBackend(Kitchen kitchen) {
    this.kitchen = kitchen;
    this.foodShelves = new Shelf[NUM_SHELVES];
    // create food shelves and use simple order processor and dispatcher
    foodShelves[HOT_SHELF] = new Shelf(Shelf.Type.HotFood);
    foodShelves[COLD_SHELF] = new Shelf(Shelf.Type.ColdFood);
    foodShelves[FROZEN_SHELF] = new Shelf(Shelf.Type.FrozenFood);
    foodShelves[OVERFLOW_SHELF] = new Shelf(Shelf.Type.Overflow);
  }

  // simple business logic to shelve an Order, in production it could be a
  // food service rpc call.
  public void process(Order order) {
    final Shelf shelf = order.isHot() ?
        foodShelves[HOT_SHELF] :
        (order.isCold() ? foodShelves[COLD_SHELF] : foodShelves[FROZEN_SHELF]);
    if (!shelf.addOrder(order)) {
      foodShelves[OVERFLOW_SHELF].overflow(order);
    }
  }

  public Optional<Order> pickup() {
    // FIXME
    return Optional.empty();
  }
}
