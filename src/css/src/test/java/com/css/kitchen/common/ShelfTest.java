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
    Optional<Shelf.FetchResult> fetchedOrder = hotShelf.fetchOrder(now);
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity()-1); // should be same
    assertTrue(fetchedOrder.isPresent());
    //System.out.println(fetchedOrder.get());
    Shelf.FetchResult orderResult = fetchedOrder.get();
    assertEquals(orderResult.getOrder().getName(), ramenOrder.getName());
    assertTrue(orderResult.getBackfill());
    Optional<Shelf.FetchResult> fetchedOrder2 = hotShelf.fetchOrder(now);
    assertTrue(fetchedOrder2.isPresent());
    Shelf.FetchResult orderResult2 = fetchedOrder2.get();
    assertEquals(orderResult2.getOrder().getName(), misoOrder.getName());
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity()-2); // should be -1
    Optional<Shelf.FetchResult> fetchedOrder3 = hotShelf.fetchOrder(now);
    assertTrue(fetchedOrder3.isPresent());
    Optional<Shelf.FetchResult> fetchedOrder4 = hotShelf.fetchOrder(now);
    assertFalse(fetchedOrder4.isPresent());
    assertEquals(hotShelf.getNumShelvedOrders(), 0);
  }
}
