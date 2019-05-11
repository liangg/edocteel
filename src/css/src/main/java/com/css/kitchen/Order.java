package com.css.kitchen;

import lombok.AllArgsConstructor;
import lombok.Value;

/** CSS kitchen food order */
@AllArgsConstructor
@Value
public class Order {
    private final String name;
    private final Temperature temperature;
    private final int shelfLife;
    private final double decayRate;

    enum Temperature {
        Hot,
        Cold,
        Frozen
    }

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

