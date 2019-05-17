package com.css.kitchen.common;

import lombok.Getter;
import org.joda.time.DateTimeUtils;

/**
 * It wraps the raw Order with order start time for decay value computation.
 * The "value" is what the order is valued at last shelf order queue operation.
 */
public class ShelfOrder {
  private final Order order;
  private final long submmittedAtMilli;
  @Getter private double value; // the deteriorating order value

  public ShelfOrder(Order order) {
    this.order = order;
    this.submmittedAtMilli = DateTimeUtils.currentTimeMillis();
    this.value = (double) order.getShelfLife();
  }

  // compute the current value, save in "value", and return it
  public double currentValue() {
    double orderAge = (double)(DateTimeUtils.currentTimeMillis() - submmittedAtMilli) / (double)1000;
    this.value = ((double)order.getShelfLife() - orderAge) - (order.getDecayRate() * orderAge);
    return value;
  }

  public double getNormalizedValue() {
    return this.value / (double)order.getShelfLife();
  }
}
