package com.css.kitchen;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/** CSS kitchen food order */
@AllArgsConstructor
@Builder
@Value
public class Order {
  public static final String ORDER_NAME = "name";
  public static final String ORDER_TEMP = "temp";
  public static final String ORDER_SHELFLIFE = "shelfLife";
  public static final String ORDER_DECAYRATE = "decayRate";

  private final String name;
  private final Temperature temperature;
  private final int shelfLife; // seconds
  private final double decayRate;

  public enum Temperature {
    Hot,
    Cold,
    Frozen
  }

  // valid map of Temperature string value
  static public ImmutableMap<String, Temperature> temperatureMap = ImmutableMap.of(
      "hot", Temperature.Hot, "cold", Temperature.Cold, "frozen", Temperature.Frozen);

  public boolean isHot() { return temperature == Temperature.Hot; }
  public boolean isCold() { return temperature == Temperature.Cold; }
  public boolean isFrozen() { return temperature == Temperature.Frozen; }

  public double getValue(int orderAge) {
    return (double)(shelfLife - orderAge) - (decayRate * orderAge);
  }

  public double getNormalizedValue(int orderAge) {
    return getValue(orderAge) / (double)shelfLife;
  }
}

