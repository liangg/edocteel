package com.css.kitchen;

/**
 * Kitchen food order dispatch interface that can be implemented with different
 * dispatcher strategy.
 */
public interface OrderDispatch {
    public Order dispatchOrder();
}