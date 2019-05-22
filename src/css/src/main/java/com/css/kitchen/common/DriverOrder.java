package com.css.kitchen.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * CSS kitchen driver delivery work order.
 *
 * In production code, this should be a IDL (Thrift or protobuf) struct with
 * modern Java class code generation.
 */
@AllArgsConstructor
@Builder
@Value
public class DriverOrder {
  final private Long orderId;
  final private Order.FoodType orderType;
}
