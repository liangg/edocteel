package com.css.kitchen.impl;

import com.css.kitchen.common.Order;
import com.css.kitchen.util.MetricsManager;

import com.google.common.base.Preconditions;
import lombok.Getter;
import java.lang.InterruptedException;
import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;
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
    Preconditions.checkState(shelfType != Type.Overflow && order != null);
    boolean result = add(order);
    logger.debug(String.format("Shelf-%s add order %s: %s", shelfType, result ? "okay" : "full", order));
    return result;
  }

  private boolean add(Order order) {
    if (order == null)
      return false;
    ShelfOrder shelfOrder = new ShelfOrder(order);
    boolean result = false;
    try {
      lock.tryLock(1, TimeUnit.SECONDS);
      if (shelvedOrders.size() < this.capacity) {
        shelvedOrders.add(shelfOrder);
        result = true;
      }
    } catch (InterruptedException e) {
      System.out.println("Exception in adding order to the Shelf");
      result = false;
      MetricsManager.incr(MetricsManager.SHELF_ORDER_ERRORS);
    } finally {
      lock.unlock();
    }
    return result;
  }

  // Fetch a ready Order from the normal shelf and keep the Overflow shelf internal
  public Optional<FetchResult> fetchOrder(long now) {
    Preconditions.checkState(shelfType != Type.Overflow);
    return fetch(now);
  }

  private Optional<FetchResult> fetch(long now) {
    FetchResult result = null;
    try {
      lock.tryLock(1, TimeUnit.SECONDS);
      if (shelvedOrders.size() > 0) {
        boolean backfill = shelvedOrders.size() >= this.capacity;
        ShelfOrder order = maxValueOrder(now);
        result = new FetchResult(order.getOrder(), backfill);
        shelvedOrders.remove(order);
      }
    } catch (InterruptedException e) {
      System.out.println("Exception in fetching order from the Shelf");
    } finally {
      lock.unlock();
    }
    return Optional.empty().ofNullable(result);
  }

  public void overflow(Order order) {
    Preconditions.checkState(shelfType == Type.Overflow);
    if (!add(order)) {
      // resolve and unblock order fullfillment
      resolve(order);
    }
  }

  // Resolve the blocked order by choosing an Order to discard
  private void resolve(Order order) {
    // simple resolution is to discard the new order
    MetricsManager.incr(MetricsManager.WASTED_ORDERS);
  }

  public Optional<ShelfOrder> getBackfill() {
    Preconditions.checkState(shelfType == Type.Overflow);
    return Optional.empty();
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

  public int getNumShelvedOrders() { return shelvedOrders.size(); }

  /* Order fetch result indicate the need of backfill from Overflow shelf */
  static public class FetchResult {
    @Getter private final Order order;
    @Getter private final Boolean backfill;

    public FetchResult(Order order, boolean backfill) {
      this.order = order;
      this.backfill = backfill;
    }
  }
}
