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
    hotShelf.addOrder(ramenOrder, 1L);
    assertEquals(hotShelf.getNumShelvedOrders(), 1);
    hotShelf.addOrder(misoOrder, 2L);
    assertEquals(hotShelf.getNumShelvedOrders(), 2);
    hotShelf.addOrder(burgerOrder, 3L);
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity());
    hotShelf.addOrder(tofuSoupOrder, 4L);
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity());

    long now = DateTimeUtils.currentTimeMillis();
    Optional<Shelf.FetchResult> fetchedOrder = hotShelf.fetchOrder(1L);
    assertTrue(fetchedOrder.isPresent());
    Shelf.FetchResult orderResult = fetchedOrder.get();
    assertEquals(orderResult.getOrder().getName(), ramenOrder.getName());
    assertTrue(orderResult.getBackfill());
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity()-1); // should be same with backfill

    Optional<Shelf.FetchResult> fetchedOrder2 = hotShelf.fetchOrder(2L);
    assertTrue(fetchedOrder2.isPresent());
    Shelf.FetchResult orderResult2 = fetchedOrder2.get();
    assertEquals(orderResult2.getOrder().getName(), misoOrder.getName());
    assertEquals(hotShelf.getNumShelvedOrders(), 1); // should be 2

    Optional<Shelf.FetchResult> fetchedOrder3 = hotShelf.fetchOrder(3L);
    assertTrue(fetchedOrder3.isPresent());
    assertEquals(hotShelf.getNumShelvedOrders(), 0); // should be 1

    Optional<Shelf.FetchResult> fetchedOrder4 = hotShelf.fetchOrder(4L);
    assertFalse(fetchedOrder4.isPresent());
    assertEquals(hotShelf.getNumShelvedOrders(), 0);
  }
}
