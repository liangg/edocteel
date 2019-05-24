package com.css.kitchen.impl;

import com.css.kitchen.common.Order;
import com.css.kitchen.util.MetricsManager;

import com.google.common.base.Preconditions;
import lombok.Getter;
import java.lang.InterruptedException;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
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
  final private ConcurrentHashMap<Long, ShelfOrder> shelvedOrders; // (orderId -> ShelfOrder)

  public Shelf(Type type, int capacity) {
    this.shelfType = type;
    this.capacity = capacity;
    this.shelvedOrders = new ConcurrentHashMap<>();
  }

  public Shelf(Type type) {
    this(type, type == Type.Overflow ? OVERFLOW_SIZE : SHELF_SIZE);
  }

  public boolean isOverflow() { return this.shelfType == Type.Overflow; }

  // Add a new Order to a normal shelf and keep the Overflow shelf internal
  public boolean addOrder(Order order, long orderId) {
    Preconditions.checkState(shelfType != Type.Overflow && order != null);
    ShelfOrder shelfOrder = new ShelfOrder(order, orderId);
    boolean result = add(shelfOrder);
    logger.debug(String.format("Shelf-%s add order(%d) %s: %s", shelfType, orderId, result ? "okay" : "full", order));
    return result;
  }

  // Backfill a ShelfOrder from the Overflow shelf
  public boolean backfillOrder(ShelfOrder shelfOrder) {
    Preconditions.checkState(shelfType != Type.Overflow && shelfOrder != null);
    boolean result = add(shelfOrder);
    logger.debug(String.format("Shelf-%s backfill order(%d) %s: %s",
        shelfType, shelfOrder.getOrderId(), result ? "okay" : "full", shelfOrder.getOrder()));
    return result;
  }

  private boolean add(ShelfOrder shelfOrder) {
    Preconditions.checkState(shelfOrder != null);
    boolean result = false;
    try {
      lock.tryLock(1, TimeUnit.SECONDS);
      if (shelvedOrders.size() < this.capacity) {
        shelvedOrders.put(Long.valueOf(shelfOrder.getOrderId()), shelfOrder);
        result = true;
      }
    } catch (InterruptedException e) {
      System.out.println("Exception in adding order to the Shelf");
      result = false;
      MetricsManager.incr(MetricsManager.SHELF_ORDER_ERRORS);
    } finally {
      lock.unlock();
    }
    if (result)
      printShelf();
    return result;
  }

  public Optional<FetchResult> fetchOrder(Long orderId) {
    Preconditions.checkState(orderId != null);
    FetchResult result = null;
    try {
      lock.tryLock(1, TimeUnit.SECONDS);
      if (shelvedOrders.size() > 0 && shelvedOrders.containsKey(orderId)) {
        // backfill is required for normal shelf only
        boolean backfill = shelvedOrders.size() >= this.capacity && shelfType != Type.Overflow;
        ShelfOrder order = shelvedOrders.get(orderId);
        result = new FetchResult(order.getOrder(), backfill);
        shelvedOrders.remove(orderId);
      }
    } catch (InterruptedException e) {
      System.out.println("Exception in fetching order from the Shelf");
    } finally {
      lock.unlock();
    }
    if (result != null)
      printShelf();
    return Optional.empty().ofNullable(result);
  }

  public void overflow(Order order, long orderId, long now) {
    Preconditions.checkState(shelfType == Type.Overflow);
    final ShelfOrder shelfOrder = new ShelfOrder(order, orderId);
    if (!add(shelfOrder)) {
      // resolve and unblock order fulfillment
      resolve(order, orderId, now);
    }
  }

  // Resolve the blocked order by choosing an Order to discard
  private void resolve(Order order, long orderId, long now) {
    Preconditions.checkState(shelfType == Type.Overflow);
    // simple resolution is to discard the new order, but we optimize for minimizing the
    // number of wasted order by discarding an order that will most likely waste
    try {
      PriorityQueue<ShelfOrder> priorityQueue =
          new PriorityQueue<ShelfOrder>(OVERFLOW_SIZE, new ShelfOrder.ShelfOrderComparator());
      lock.lock();
      shelvedOrders.forEach((k, v) -> {
        // compute the current value for each order on the shelf
        v.computeAndSetValue(now, isOverflow());
        // prepare to discard orders whose values have diminished to zero
        if (v.getValue() > 0) {
          priorityQueue.add(v);
        } else {
          logger.debug(String.format("Discard order(%d): %s", v.getOrderId(), v.getOrder()));
          MetricsManager.incr(MetricsManager.WASTED_ORDERS);
        }
      });

      // it either has vacated slot or has to discard an order that has the smallest value
      shelvedOrders.entrySet().removeIf(e -> Double.compare(e.getValue().getValue(), 0f) == 0);
      if (shelvedOrders.size() == capacity) {
        ShelfOrder discardedOrder = priorityQueue.peek();
        if (discardedOrder != null) {
          shelvedOrders.remove(discardedOrder.getOrderId());
          logger.debug(String.format("Discard overflow order(%d): %s",
              discardedOrder.getOrderId(), discardedOrder.getOrder()));
          MetricsManager.incr(MetricsManager.WASTED_ORDERS);
        }
      }
      add(new ShelfOrder(order, orderId));
    } finally {
      lock.unlock();
    }
    MetricsManager.incr(MetricsManager.WASTED_ORDERS);
  }

  public Optional<ShelfOrder> getBackfill(Order.FoodType orderType, long now) {
    Preconditions.checkState(shelfType == Type.Overflow);
    ShelfOrder backfillOrder = null;
    try {
      PriorityQueue<ShelfOrder> priorityQueue =
          new PriorityQueue<ShelfOrder>(OVERFLOW_SIZE, new ShelfOrder.ShelfOrderComparator());

      lock.lock();
      shelvedOrders.forEach((k, v) -> {
        // compute the current value for each order on the shelf
        v.computeAndSetValue(now, isOverflow());
        // prepare to discard orders whose values have diminished to zero
        if (v.getValue() <= 0) {
          logger.debug(String.format("Discard order(%d): %s", v.getOrderId(), v.getOrder()));
          MetricsManager.incr(MetricsManager.WASTED_ORDERS);
        }
        if (v.getOrder().getType() == orderType && v.getValue() > 0) {
          priorityQueue.add(v);
        }
      });

      // remove discarded orders from the Overflow shelf
      shelvedOrders.entrySet().removeIf(e -> Double.compare(e.getValue().getValue(), 0f) == 0);

      backfillOrder = priorityQueue.peek();
      if (backfillOrder != null) {
        shelvedOrders.remove(backfillOrder.getOrderId());
      }
    } finally {
      lock.unlock();
    }
    if (backfillOrder != null)
      printShelf();
    return Optional.ofNullable(backfillOrder);
  }

  // used for testing
  public int getNumShelvedOrders() { return shelvedOrders.size(); }

  public void printShelf() {
    StringBuilder output = new StringBuilder();
    output.append(shelfType);
    output.append(" {");
    shelvedOrders.forEach( (k,v) -> {
      output.append(v.toString());
    });
    output.append("}\n");
    logger.info(output.toString());
  }

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
