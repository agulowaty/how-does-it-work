package com.github.agulowaty.playground.proxy;

import com.github.agulowaty.playground.proxy.cache.MethodResultCacheKey;
import com.github.benmanes.caffeine.cache.Cache;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public final class MethodResultCachingInvocationHandler implements InvocationHandler {

    private final Cache<MethodResultCacheKey, Object> cache;
    private final Object target;

    public MethodResultCachingInvocationHandler(Object target, final Cache<MethodResultCacheKey, Object> cache) {
        this.target = target;
        this.cache = cache;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (!isCacheable(method, args))
            return method.invoke(target);

        MethodResultCacheKey cacheKey = new MethodResultCacheKey(method, args);
        return cache.get(cacheKey, k -> {
            try {
                return method.invoke(target, args);
            } catch (ReflectiveOperationException e) {
                return null;
            }
        });
    }

    /**
     * Check if caching should be applied at all.
     * Finds matching method on the target object's class and looks up respective annotation.
     * In addition to that, non-empty arguments array is required so cache key can be generated based on it's values.
     */
    private boolean isCacheable(final Method method, final Object[] args) {
        Method targetMethod =
                MethodUtils.getMatchingAccessibleMethod(
                        target.getClass(),
                        method.getName(),
                        method.getParameterTypes()
                );
        boolean canProduceCacheKey = args.length != 0;
        return canProduceCacheKey && targetMethod.isAnnotationPresent(Cacheable.class);
    }
}
