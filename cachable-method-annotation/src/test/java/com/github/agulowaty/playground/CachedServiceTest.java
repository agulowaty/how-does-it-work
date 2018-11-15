package com.github.agulowaty.playground;

import com.github.agulowaty.playground.beans.Container;
import com.google.common.hash.Hasher;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.FIVE_HUNDRED_MILLISECONDS;
import static org.awaitility.Duration.TWO_SECONDS;


public class CachedServiceTest {

    private final Container beans = new Container();
    private Orders orders;

    @Before
    public void injectBeans() throws Exception {
        beans.bindToInstance(Orders.class, new DistantOrdersProvider());
        orders = beans.getBean(Orders.class);
    }

    @Test
    public void resultsOfOrdersListingCallAreCached() throws ExecutionException, InterruptedException {
        String customerId = "super-customer";
        // first time it may be longer, allow it to cache results..
        await().atMost(TWO_SECONDS.plus(TWO_SECONDS)).until(orders.orders(customerId)::isDone);

        // second time for the same customer should be blazing fast...
        await().atMost(FIVE_HUNDRED_MILLISECONDS).until(orders.orders(customerId)::isDone);
    }

    @Test
    public void ordersCachingIsUnique() throws ExecutionException, InterruptedException {
        // this should be long
        await().atMost(TWO_SECONDS.plus(TWO_SECONDS)).until(orders.orders("customer-a")::isDone);

        // this should be faster as we're asking for the same customer
        await().atMost(FIVE_HUNDRED_MILLISECONDS).until(orders.orders("customer-a")::isDone);

        // this should be longer because we're asking for different customer
        await().atLeast(TWO_SECONDS).until(orders.orders("customer-b")::isDone);
    }
}
