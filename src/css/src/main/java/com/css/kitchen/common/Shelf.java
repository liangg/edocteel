package com.css.kitchen.common;

import com.css.kitchen.util.MetricsManager;
import lombok.Getter;

import java.lang.InterruptedException;
import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread-safe CSS kitchen food order shelf.
 */
@Getter
public class Shelf {
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
  @Getter private int numShelvedOrders = 0;

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

  public boolean add(Order order) {
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

  public Optional<Order> fetch(long now) {
    Order result = null;
    try {
      lock.tryLock(1, TimeUnit.SECONDS);
      if (shelvedOrders.size() > 0) {
        ShelfOrder order = maxValueOrder(now);
        result = order.getOrder();
        shelvedOrders.remove(order);
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
      priorityQueue.add(o);
    });
    return priorityQueue.peek();
  }

  public boolean overflow(Order order) {
    return false;
  }
}
