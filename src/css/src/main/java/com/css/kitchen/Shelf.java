package com.css.kitchen;

import java.lang.InterruptedException;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A thread-safe CSS kitchen food shelf.
 */
public class Shelf {
    public static int SHELF_SIZE = 15;
    public static int OVERFLOW_SIZE = 20;

    enum Type {
        HotFood,
        ColdFood,
        FrozenFood,
        Overflow
    }

    final public Type shelfType;
    final private int capacity;
    final private ArrayDeque<Order> shelvedOrders;
    private int size = 0;
    final private Semaphore semaphore = new Semaphore(1);

    public Shelf(Type type, int capacity) {
        this.shelfType = type;
        this.capacity = capacity;
        this.shelvedOrders = new ArrayDeque<>(capacity);
    }

    public Shelf(Type type) {
        this(type, type == Type.Overflow ? OVERFLOW_SIZE : SHELF_SIZE);
    }

    public int getCapacity() { return this.capacity; }

    public boolean add(Order order) {
        if (order == null)
            return false;
        boolean result = false;
        try {
            semaphore.acquire(); // thread-safe
            if (shelvedOrders.size() < this.capacity) {
                shelvedOrders.addLast(order);
                size += 1;
                result = true;
            }
            semaphore.release();
        } catch (InterruptedException e) {
            System.out.println("Shelf add order exception");
        }
        return result;
    }

    public Optional<Order> fetch() {
        Order result = null;
        try {
            semaphore.acquire(); // thread-safe
            if (size > 0) {
                result = shelvedOrders.getFirst();
                size -= 1;
            }
            semaphore.release();
        } catch (InterruptedException e) {
            System.out.println("Shelf get order exception");
        }
        return Optional.empty().ofNullable(result);
    }

}
