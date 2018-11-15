package com.github.agulowaty.playground;

import java.util.List;
import java.util.concurrent.Future;

public interface Orders {
    /**
     * List all customer orders.
     * Must respond ASAP !
     */
    Future<List<Order>> orders(String customerId);

    class Order {
    }
}
