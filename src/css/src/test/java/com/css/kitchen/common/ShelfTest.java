package com.css.kitchen.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.css.kitchen.Kitchen;
import com.css.kitchen.common.Order;
import com.css.kitchen.impl.Shelf;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;

public class ShelfTest {
  private Order ramenOrder;
  private Order misoOrder;
  private Order burgerOrder;
  private Order tofuSoupOrder;

  @Before
  public void init() {
    ramenOrder = Order.builder()
        .name("Ramen")
        .temperature(Order.Temperature.Hot)
        .shelfLife(500)
        .decayRate(0.45)
        .build();
    misoOrder = Order.builder()
        .name("seafood miso")
        .temperature(Order.Temperature.Hot)
        .shelfLife(600)
        .decayRate(0.35)
        .build();
    burgerOrder = Order.builder()
        .name("Duper Burger")
        .temperature(Order.Temperature.Hot)
        .shelfLife(1200)
        .decayRate(0.10)
        .build();
    tofuSoupOrder = Order.builder()
        .name("Tofu soup")
        .temperature(Order.Temperature.Hot)
        .shelfLife(1000)
        .decayRate(0.30)
        .build();
  }

  @Test
  public void testAddShelf() {
    Shelf hotShelf = new Shelf(Shelf.Type.HotFood, 3);
    hotShelf.addOrder(ramenOrder);
    hotShelf.addOrder(misoOrder);
    hotShelf.addOrder(burgerOrder);
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity());
    hotShelf.addOrder(tofuSoupOrder);
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity());

    long now = DateTimeUtils.currentTimeMillis();
    Optional<Order> fetchedOrder = hotShelf.fetchOrder(now);
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity()-1); // should be same
    assertTrue(fetchedOrder.isPresent());
    //System.out.println(fetchedOrder.get());
    assertEquals(fetchedOrder.get().getName(), ramenOrder.getName());
    fetchedOrder = hotShelf.fetchOrder(now);
    assertTrue(fetchedOrder.isPresent());
    assertEquals(fetchedOrder.get().getName(), misoOrder.getName());
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity()-2); // should be -1
    fetchedOrder = hotShelf.fetchOrder(now);
    assertTrue(fetchedOrder.isPresent());
    fetchedOrder = hotShelf.fetchOrder(now);
    assertFalse(fetchedOrder.isPresent());
    assertEquals(hotShelf.getNumShelvedOrders(), 0);
  }
}
