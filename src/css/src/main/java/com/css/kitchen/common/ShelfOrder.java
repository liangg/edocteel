package com.css.kitchen.common;

import lombok.Getter;
import org.joda.time.DateTimeUtils;

import java.lang.Override;
import java.util.Comparator;

/**
 * It wraps the raw Order with order start time for decay value computation.
 * The "value" is what the order is valued at last shelf order queue operation.
 */
public class ShelfOrder {
  @Getter private final Order order;
  private final long submmittedAtMilli;
  @Getter private double value; // the deteriorating order value

  static class ShelfOrderComparator implements Comparator<ShelfOrder> {
    @Override
    public int compare(ShelfOrder x, ShelfOrder y) {
      return Double.compare(x.getValue(), y.getValue());
    }
  }

  public ShelfOrder(Order order) {
    this.order = order;
    this.submmittedAtMilli = DateTimeUtils.currentTimeMillis();
    this.value = (double) order.getShelfLife();
  }

  // compute the current value, save in "value", and return it
  public double setCurrentValue(long now, boolean overflow) {
    int multiply = overflow ? 2 : 1;
    double age = (double)(now - submmittedAtMilli) / (double)1000;
    this.value = ((double)order.getShelfLife() - age) - (order.getDecayRate() * multiply * age);
    return value;
  }

  public double normalizedValue() {
    return this.value / (double)order.getShelfLife();
  }

  @Override
  public int hashCode() {
    return order.hashCode();
  }
}
