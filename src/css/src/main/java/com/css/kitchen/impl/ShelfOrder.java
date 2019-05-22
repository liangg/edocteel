package com.css.kitchen.impl;

import com.css.kitchen.common.Order;

import lombok.Getter;
import java.lang.Override;
import java.util.Comparator;
import org.joda.time.DateTimeUtils;

/**
 * It wraps the raw Order with order start time for decay value computation.
 * The "value" is what the order is valued at last shelf order queue operation.
 */
public class ShelfOrder {
  @Getter private final long orderId;
  @Getter private final Order order;
  @Getter private double value; // the deteriorating order value
  private int shelfLife;
  private final long lastValuedAtMilli;

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
    this.shelfLife = (double) order.getShelfLife();
    this.lastValuedAtMilli = DateTimeUtils.currentTimeMillis();
  }

  //
  public void transferShelf() {

  }

  // compute the current value, save in "value", and return it
  // value = (shelf_life - order_age) - decay_rate * order_age
  public double setCurrentValue(long now, boolean overflow) {
    // FIXME: diminishing value from overflow to shelf
    int multiply = overflow ? 2 : 1;
    double age = (double)(now - lastValuedAtMilli) / (double)1000;
    this.value = ((double)order.shelfLife - age) - (order.getDecayRate() * multiply * age);
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
