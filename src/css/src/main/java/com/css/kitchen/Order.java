package com.css.kitchen;

/** CSS kitchen food order */
public class Order {
    final String name;
    final Temperature temperature;
    final int shelfLife;
    final double decayRate;

    enum Temperature {
        Hot,
        Cold,
        Frozen
    }

    // This should be generated using lombok
    public Order(String name, Temperature temp, int shelfLife, double decayRate) {
        this.name = name;
        this.temperature = temp;
        this.shelfLife = shelfLife;
        this.decayRate = decayRate;
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

