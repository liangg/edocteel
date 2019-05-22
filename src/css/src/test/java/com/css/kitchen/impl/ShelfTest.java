package com.css.kitchen.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.css.kitchen.Kitchen;
import com.css.kitchen.common.Order;
import com.css.kitchen.impl.Shelf;
import com.css.kitchen.impl.ShelfOrder;
import java.util.HashSet;
import java.util.Optional;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;

public class ShelfTest {
  private Order ramenOrder;
  private Order misoOrder;
  private Order burgerOrder;
  private Order tofuSoupOrder;
  private Order cheesecakeOrder;
  private Order icecreamOrder;

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
  }

  @Test
  public void testShelfOrder() {
    ShelfOrder.ShelfOrderComparator comparator = new ShelfOrder.ShelfOrderComparator();

    ShelfOrder order1 = new ShelfOrder(burgerOrder, 20L);
    ShelfOrder order2 = new ShelfOrder(tofuSoupOrder, 28L);
    assertTrue(comparator.compare(order1, order2) > 0);
    ShelfOrder order3 = new ShelfOrder(ramenOrder, 50L);
    assertTrue(comparator.compare(order3, order2) < 0);

    assertTrue(Double.compare(order1.getValue(), (double) burgerOrder.getShelfLife()) == 0);
    assertTrue(Double.compare(order2.getValue(), (double) tofuSoupOrder.getShelfLife()) == 0);
    assertTrue(Double.compare(order3.getValue(), (double) ramenOrder.getShelfLife()) == 0);

    long future = DateTimeUtils.currentTimeMillis() + 10000; // 10 sec from now
    double order1Value = order1.computeAndSetValue(future, false);
    assertTrue(Double.compare(order1.getValue(), (double) burgerOrder.getShelfLife()) < 0);

    ShelfOrder order11 = new ShelfOrder(burgerOrder, 20L);
    double order11Value = order11.computeAndSetValue(future, true); // overflow
    assertTrue(Double.compare(order1Value, order11Value) > 0); // normal shelf has longer life than overflow
  }

  @Test
  public void testValueDecay() {
    Order hotpotOrder = Order.builder()
        .name("Hotpot")
        .type(Order.FoodType.Hot)
        .shelfLife(10000)
        .decayRate(0.25)
        .build();
    ShelfOrder shelfOrder = new ShelfOrder(hotpotOrder, 100L);
    long now = DateTimeUtils.currentTimeMillis();
    double value = shelfOrder.computeAndSetValue(now+1000000, true); // overflow shelf
    assertTrue(Double.compare(value, shelfOrder.getValue()) == 0);
    double value2 = shelfOrder.computeAndSetValue(now+2000000, false);
    assertTrue(Double.compare(value2, shelfOrder.getValue()) == 0);
    assertTrue(Double.compare(value, value2) > 0);
    double diff = 10000 - value;
    double diff2 = value - value2;
    System.out.println(String.format("diff %f,%f - %f,%f", value, value2, diff, diff2));
    assertTrue(Double.compare(diff, diff2) > 0);
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
    assertEquals(hotShelf.getNumShelvedOrders(), hotShelf.getCapacity()-1); // no backfill

    Optional<Shelf.FetchResult> fetchedOrder2 = hotShelf.fetchOrder(2L);
    assertTrue(fetchedOrder2.isPresent());
    Shelf.FetchResult orderResult2 = fetchedOrder2.get();
    assertEquals(orderResult2.getOrder().getName(), misoOrder.getName());
    assertEquals(hotShelf.getNumShelvedOrders(), 1);

    Optional<Shelf.FetchResult> fetchedOrder3 = hotShelf.fetchOrder(3L);
    assertTrue(fetchedOrder3.isPresent());
    assertEquals(hotShelf.getNumShelvedOrders(), 0);

    Optional<Shelf.FetchResult> fetchedOrder4 = hotShelf.fetchOrder(4L);
    assertFalse(fetchedOrder4.isPresent());
    assertEquals(hotShelf.getNumShelvedOrders(), 0);
  }

  @Test
  public void testBackfill() {
    Shelf overflowShelf = new Shelf(Shelf.Type.Overflow, 4);
    overflowShelf.overflow(ramenOrder, 1L);
    overflowShelf.overflow(icecreamOrder, 2L);
    overflowShelf.overflow(burgerOrder, 3L);
    overflowShelf.overflow(cheesecakeOrder, 4L);
    assertEquals(overflowShelf.getNumShelvedOrders(), overflowShelf.getCapacity());

    long now = DateTimeUtils.currentTimeMillis() + 2000;
    Optional<ShelfOrder> backfill = overflowShelf.getBackfill(Order.FoodType.Cold, now);
    assertTrue(backfill.isPresent());
    assertEquals(backfill.get().getOrder().getName(), cheesecakeOrder.getName());
    System.out.println("overflow shelf " + overflowShelf.getNumShelvedOrders());
    assertTrue(overflowShelf.getNumShelvedOrders() < 4);

    long now2 = DateTimeUtils.currentTimeMillis() + 4000;
    Optional<ShelfOrder> backfillHot = overflowShelf.getBackfill(Order.FoodType.Hot, now2);
    assertTrue(backfillHot.isPresent());
    assertEquals(backfillHot.get().getOrder().getName(), ramenOrder.getName()); // smaller value
    System.out.println("overflow shelf " + overflowShelf.getNumShelvedOrders());
    assertTrue(overflowShelf.getNumShelvedOrders() < 3);

    long now3 = DateTimeUtils.currentTimeMillis() + 5000;
    Optional<ShelfOrder> backfillHot2 = overflowShelf.getBackfill(Order.FoodType.Hot, now3);
    assertTrue(backfillHot2.isPresent());
    assertEquals(backfillHot2.get().getOrder().getName(), burgerOrder.getName()); // smaller value
    System.out.println("overflow shelf " + overflowShelf.getNumShelvedOrders());
    assertTrue(overflowShelf.getNumShelvedOrders() < 2);
  }
}
