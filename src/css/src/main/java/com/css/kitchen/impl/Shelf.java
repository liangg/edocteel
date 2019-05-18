package com.css.kitchen.impl;

import com.css.kitchen.common.Order;
import com.css.kitchen.util.MetricsManager;

import com.google.common.base.Preconditions;
import lombok.Getter;
import java.lang.InterruptedException;
import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A thread-safe CSS kitchen food order shelf.
 */
@Getter
public class Shelf {
  private static Logger logger = LoggerFactory.getLogger(Shelf.class);
  public static int SHELF_SIZE = 15;
  public static int OVERFLOW_SIZE = 20;

  public enum Type {
    HotFood,
    ColdFood,
    FrozenFood,
    Overflow
  }

  @Getter final private Type shelfType;
  @Getter final private int capacity;
  final private ReentrantLock lock = new ReentrantLock();
  final private HashSet<ShelfOrder> shelvedOrders;

  final private Semaphore semaphore = new Semaphore(1);

  public Shelf(Type type, int capacity) {
    this.shelfType = type;
    this.capacity = capacity;
    this.shelvedOrders = new HashSet<>(capacity);
  }

  public Shelf(Type type) {
    this(type, type == Type.Overflow ? OVERFLOW_SIZE : SHELF_SIZE);
  }

  public boolean isOverflow() { return this.shelfType == Type.Overflow; }

  // Add a new Order to a normal shelf and keep the Overflow shelf internal
  public boolean addOrder(Order order) {
    Preconditions.checkState(shelfType != Type.Overflow);
    return add(order);
  }

  private boolean add(Order order) {
    if (order == null)
      return false;
    ShelfOrder shelfOrder = new ShelfOrder(order);
    boolean result = true;
    try {
      lock.tryLock(1, TimeUnit.SECONDS);
      if (shelvedOrders.size() < this.capacity) {
        shelvedOrders.add(shelfOrder);
      }
    } catch (InterruptedException e) {
      System.out.println("Exception in adding order to the Shelf");
      MetricsManager.incr(MetricsManager.SHELF_ORDER_ERRORS);
      result = false;
    } finally {
      lock.unlock();
    }
    return result;
  }

  // Fetch a ready Order from the normal shelf and keep the Overflow shelf internal
  public Optional<Order> fetchOrder(long now) {
    Preconditions.checkState(shelfType != Type.Overflow);
    return fetch(now);
  }

  private Optional<Order> fetch(long now) {
    Order result = null;
    try {
      lock.tryLock(1, TimeUnit.SECONDS);
      if (shelvedOrders.size() > 0) {
        boolean backfill = shelvedOrders.size() >= this.capacity;
        ShelfOrder order = maxValueOrder(now);
        result = order.getOrder();
        shelvedOrders.remove(order);
        // backfill an order from the Overflow shelf
        if (backfill) {

        }
      }
      semaphore.release();
    } catch (InterruptedException e) {
      System.out.println("Exception in fetching order from the Shelf");
    } finally {
      lock.unlock();
    }
    return Optional.empty().ofNullable(result);
  }

  private ShelfOrder maxValueOrder(long now) {
    PriorityQueue<ShelfOrder> priorityQueue =
        new PriorityQueue<ShelfOrder>(OVERFLOW_SIZE, new ShelfOrder.ShelfOrderComparator());
    shelvedOrders.forEach( o -> {
      o.setCurrentValue(now, isOverflow());
      logger.debug(String.format("shelf order value as of %d: %s", now, o));
      priorityQueue.add(o);
    });
    return priorityQueue.peek();
  }

  public boolean overflow(Order order) {
    Preconditions.checkState(shelfType == Type.Overflow);

    return false;
  }

  public int getNumShelvedOrders() { return shelvedOrders.size(); }
}
