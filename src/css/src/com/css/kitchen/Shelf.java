package com.css.kitchen;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A thread-safe CSS kitchen food shelf.
 */
public class Shelf {
    enum Type {
        HotFood,
        ColdFood,
        FrozenFood,
        Overflow
    }

    final public Type shelfType;
    final private int capacity;
    final private Semaphore semaphore = new Semaphore(1);
    final private ArrayDeque<Order> shelvedOrders;
    private  int size = 0;

    public Shelf(Type type, int capacity) {
        this.type = type;
        this.capacity = capacity;
        this.shelvedOrders = new ArrayDeque<>(capacity);
    }

    public boolean add(Order order) {
        if (order == null)
            return false;
        boolean result = false;
        semaphore.acquire(); // thread-safe
        if (shelvedOrders.size() < this.capacity) {
            shelvedOrders.addLast(order);
            size += 1;
            result = true;
        }
        semaphore.release();
        return result;
    }

    public Optional<Order> fetch() {
        Order result = null;
        semaphore.acquire(); // thread-safe
        if (size > 0) {
            result = shelvedOrders.getFirst();
            size -= 1;
        }
        semaphore.release();
        return Optional.empty().ofNullable(result);
    }

}
