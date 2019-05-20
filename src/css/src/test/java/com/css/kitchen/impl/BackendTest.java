package com.css.kitchen.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.css.kitchen.Kitchen;
import com.css.kitchen.common.DriverOrder;
import com.css.kitchen.common.Order;
import com.css.kitchen.impl.OrderBackend;
import com.css.kitchen.impl.Shelf;
import com.css.kitchen.impl.ShelfOrder;
import java.util.HashSet;
import java.util.Optional;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;

public class BackendTest {
  private Order ramenOrder;
  private Order misoOrder;
  private Order burgerOrder;
  private Order tofuSoupOrder;
  private Order cheesecakeOrder;
  private Order icecreamOrder;

  private Kitchen kitchen;
  private OrderBackend backend;

  @Before
  public void init() {
    ramenOrder = Order.builder()
        .name("Ramen")
        .type(Order.Temperature.Hot)
        .shelfLife(500)
        .decayRate(0.45)
        .build();
    misoOrder = Order.builder()
        .name("seafood miso")
        .type(Order.Temperature.Hot)
        .shelfLife(600)
        .decayRate(0.35)
        .build();
    burgerOrder = Order.builder()
        .name("Duper Burger")
        .type(Order.Temperature.Hot)
        .shelfLife(1200)
        .decayRate(0.10)
        .build();
    tofuSoupOrder = Order.builder()
        .name("Tofu soup")
        .type(Order.Temperature.Hot)
        .shelfLife(1000)
        .decayRate(0.30)
        .build();
    cheesecakeOrder = Order.builder()
        .name("Cheesecake")
        .type(Order.Temperature.Cold)
        .shelfLife(700)
        .decayRate(0.50)
        .build();
    icecreamOrder = Order.builder()
        .name("Icecream rockyroad")
        .type(Order.Temperature.Frozen)
        .shelfLife(120)
        .decayRate(1.0)
        .build();

    kitchen = new Kitchen();
    backend = new OrderBackend(kitchen, 3, 4);
  }

  public void testPickup() {
    backend.process(ramenOrder);
    backend.process(tofuSoupOrder);
    backend.process(misoOrder);
    backend.process(burgerOrder);
    backend.process(cheesecakeOrder);

  }
}
