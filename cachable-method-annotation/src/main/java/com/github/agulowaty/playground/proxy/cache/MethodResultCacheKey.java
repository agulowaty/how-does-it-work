package com.github.agulowaty.playground.proxy.cache;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.lang.reflect.Method;

public final class MethodResultCacheKey {
    private final Method method;
    private final Object[] args;


    public MethodResultCacheKey(Method method, Object[] args) {
        this.method = method;
        this.args = args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || this.getClass() != o.getClass()) return false;

        MethodResultCacheKey that = (MethodResultCacheKey) o;

        return new EqualsBuilder()
                .append(this.method, that.method)
                .append(this.args, that.args)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(this.method)
                .append(this.args)
                .toHashCode();
    }
}
