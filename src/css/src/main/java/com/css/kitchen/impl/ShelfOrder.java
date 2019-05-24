package com.css.kitchen.impl;

import com.css.kitchen.common.Order;

import lombok.Getter;
import java.lang.Override;
import java.util.Comparator;
import org.joda.time.DateTimeUtils;


/**
 * It wraps the raw Order with a unique order id and decaying value computation.
 *
 * The "value" is eseentially the remaining shelf life time. It is a dynamically
 * changing, and its value is what was computed at last shelf order operation.
 */
public class ShelfOrder {
  @Getter private final long orderId;
  @Getter private final Order order;
  @Getter private double value; // the decaying order value
  private long lastValuedAtMilli;

  static class ShelfOrderComparator implements Comparator<ShelfOrder> {
    @Override
    public int compare(ShelfOrder x, ShelfOrder y) {
      return Double.compare(x.getValue(), y.getValue());
    }
  }

  public ShelfOrder(Order order, long id) {
    this.orderId = id;
    this.order = order;
    this.value = (double) order.getShelfLife();
    this.lastValuedAtMilli = DateTimeUtils.currentTimeMillis();
  }

  // Compute the current value, i.e. remaining shelf life time
  public double computeAndSetValue(long now, boolean overflow) {
    // overflow shelf decay 2x faster
    int multiply = overflow ? 2 : 1;
    double age = (double)(now - lastValuedAtMilli) / (double)1000;
    // formula: value = (shelf_life - order_age) - decay_rate * order_age
    value = (value - age) - (order.getDecayRate() * multiply * age);
    lastValuedAtMilli = now;
    return value;
  }

  public double normalizedValue() {
    return value / (double)order.getShelfLife();
  }

  @Override
  public int hashCode() {
    return order.hashCode();
  }

  @Override
  public String toString() {
    // display normalized value
    return String.format("[Order(%d) normalized_value(%.2f) value(%.2f) %s]",
        orderId, normalizedValue(), value, order);
  }
}
