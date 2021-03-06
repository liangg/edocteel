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
  private Order burritoOrder;

  @Before
  public void init() {
    ramenOrder = Order.builder()
        .name("Ramen")
        .type(Order.FoodType.Hot)
        .shelfLife(500)
        .decayRate(0.45)
        .build();
    misoOrder = Order.builder()
        .name("seafood miso")
        .type(Order.FoodType.Hot)
        .shelfLife(600)
        .decayRate(0.35)
        .build();
    burgerOrder = Order.builder()
        .name("Duper Burger")
        .type(Order.FoodType.Hot)
        .shelfLife(1200)
        .decayRate(0.10)
        .build();
    tofuSoupOrder = Order.builder()
        .name("Tofu soup")
        .type(Order.FoodType.Hot)
        .shelfLife(1000)
        .decayRate(0.30)
        .build();
    cheesecakeOrder = Order.builder()
        .name("Cheesecake")
        .type(Order.FoodType.Cold)
        .shelfLife(700)
        .decayRate(0.50)
        .build();
    icecreamOrder = Order.builder()
        .name("Icecream rockyroad")
        .type(Order.FoodType.Frozen)
        .shelfLife(120)
        .decayRate(1.0)
        .build();
    burritoOrder = Order.builder()
        .name("Beef Burrito")
        .type(Order.FoodType.Hot)
        .shelfLife(360)
        .decayRate(0.20)
        .build();
  }

  @Test
  public void testBackendBasicFlow() {
    Kitchen kitchen = new Kitchen();
    OrderBackend backend = new OrderBackend(kitchen, 3, 4);
    backend.process(ramenOrder);
    backend.process(tofuSoupOrder);
    backend.process(misoOrder);
    backend.process(burgerOrder); // overflow
    backend.process(cheesecakeOrder);

    final Shelf[] foodShelves = backend.getFoodShelves();
    final Shelf hotShelf = foodShelves[OrderBackend.HOT_SHELF];
    final Shelf overflowShelf = foodShelves[OrderBackend.OVERFLOW_SHELF];
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity());
    assertEquals(overflowShelf.getNumShelvedOrders(), 1);

    Optional<Order> ramenOrder = backend.pickup(new DriverOrder(1L, Order.FoodType.Hot));
    assertTrue(ramenOrder.isPresent());
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity()); // burgerOrder should be backfilled
    assertEquals(overflowShelf.getNumShelvedOrders(), 0);

    Optional<Order> burgerOrder = backend.pickup(new DriverOrder(4L, Order.FoodType.Hot));
    assertTrue(burgerOrder.isPresent());
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity()-1);

    Optional<Order> tofusoupOrder = backend.pickup(new DriverOrder(2L, Order.FoodType.Hot));
    assertTrue(tofusoupOrder.isPresent());
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity()-2);
  }

  @Test
  public void testPickupOverflow() {
    Kitchen kitchen = new Kitchen();
    OrderBackend backend = new OrderBackend(kitchen, 3, 3);
    backend.process(ramenOrder);
    backend.process(tofuSoupOrder);
    backend.process(misoOrder);
    backend.process(burgerOrder); // overflow
    backend.process(cheesecakeOrder);

    final Shelf[] foodShelves = backend.getFoodShelves();
    final Shelf hotShelf = foodShelves[OrderBackend.HOT_SHELF];
    final Shelf overflowShelf = foodShelves[OrderBackend.OVERFLOW_SHELF];
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity());
    assertEquals(overflowShelf.getNumShelvedOrders(), 1);

    Optional<Order> burgerOrder = backend.pickup(new DriverOrder(4L, Order.FoodType.Hot));
    assertTrue(burgerOrder.isPresent());
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity());
    assertEquals(overflowShelf.getNumShelvedOrders(), 0);
  }
}
