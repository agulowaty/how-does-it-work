package com.github.agulowaty.playground;

import com.github.agulowaty.playground.proxy.Cacheable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class DistantOrdersProvider implements Orders {
    /**
     * Really clunky implementation of this method, but that's all we can have for now.
     */
    @Cacheable
    @Override
    public Future<List<Order>> orders(String customerId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
                return Arrays.asList(new Order(), new Order(), new Order(), new Order());
            } catch (InterruptedException e) {
                return new ArrayList<>(0);
            }
        });

    }
}
