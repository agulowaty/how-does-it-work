package com.github.agulowaty.playground.beans;

import com.github.agulowaty.playground.proxy.MethodResultCachingInvocationHandler;
import com.github.agulowaty.playground.proxy.cache.MethodResultCacheKey;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Simulate simple DI container behaviour.
 */
public class Container {
    private final Map<Class<?>, InstanceBinding<?>> beans = new HashMap<>();

    // could be a bean in container, why not?
    private Cache<MethodResultCacheKey, Object> cache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .build();

    /**
     * Simple bean registration functionality - register instance for given interface
     * in Guice-like style.
     */
    public <T> void bindToInstance(final Class<T> ofClass, final T instance) {
        beans.put(ofClass, new InstanceBinding<>(instance));
    }

    /**
     * Wrap instance with proxy to decorate method invocations.
     */
    public <T> T getBean(final Class<T> ofClass) {
        Object bean = beans.get(ofClass).instance;
        return (T) Proxy.newProxyInstance(
                ofClass.getClassLoader(),
                bean.getClass().getInterfaces(),
                new MethodResultCachingInvocationHandler(bean, cache));
    }

    private static final class InstanceBinding<T> {
        final T instance;
        InstanceBinding(final T instance) {
            this.instance = instance;
        }
    }
}
