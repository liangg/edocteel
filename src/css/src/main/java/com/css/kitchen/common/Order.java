package com.css.kitchen.common;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * CSS kitchen food order.
 *
 * In production code, this should be a IDL (Thrift or protobuf) struct with
 * modern Java class code generation.
 */
@AllArgsConstructor
@Builder
@Value
public class Order {
  public static final String ORDER_NAME = "name";
  public static final String ORDER_TEMP = "temp";
  public static final String ORDER_SHELFLIFE = "shelfLife";
  public static final String ORDER_DECAYRATE = "decayRate";

  private final String name;
  private final FoodType type;
  private final int shelfLife; // seconds
  private final double decayRate;

  public enum FoodType {
    Hot,
    Cold,
    Frozen
  }

  // valid map of Temperature string value
  static public ImmutableMap<String, FoodType> temperatureMap = ImmutableMap.of(
      "hot", FoodType.Hot, "cold", FoodType.Cold, "frozen", FoodType.Frozen);

  public boolean isHot() { return type == FoodType.Hot; }
  public boolean isCold() { return type == FoodType.Cold; }
  public boolean isFrozen() { return type == FoodType.Frozen; }
}

