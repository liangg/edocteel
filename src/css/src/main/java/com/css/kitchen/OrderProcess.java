package com.css.kitchen;

/**
 * Kitchen food order process interface that can be implemented with different
 * shelving strategy.
 */
public interface OrderProcess {
    public void processOrder(Order order);
}