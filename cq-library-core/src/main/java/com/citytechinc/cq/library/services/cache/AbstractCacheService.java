/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.services.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Responsible for exposing cache stats and cache variables in <code>CacheService</code> instances.
 */
public abstract class AbstractCacheService implements CacheService {

    @Override
    public final void clearAllCaches() {
        for (final Field field : collectFields(this.getClass())) {
            if (field.getType() == Cache.class || Cache.class.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    final Cache cache = (Cache) field.get(this);
                    cache.invalidateAll();
                } catch (final Exception exception) {
                    getLogger().error(
                        "An error has occurred while attempting to invalidate cache values for {} in the class {}. See exception: {}",
                        new Object[]{ field.getName(), this.getClass().getName(), ExceptionUtils.getStackTrace(
                            exception) });
                }
            }
        }
    }

    @Override
    public final void clearSpecificCache(final String cacheVariableName) {
        for (final Field field : collectFields(this.getClass())) {
            if ((StringUtils.equals(cacheVariableName, field.getName())) && (field
                .getType() == Cache.class || Cache.class.isAssignableFrom(field.getType()))) {
                try {
                    field.setAccessible(true);
                    final Cache cache = (Cache) field.get(this);
                    cache.invalidateAll();
                } catch (final Exception exception) {
                    getLogger().error(
                        "An error has occurred while attempting to invalidate cache values for {} in the class {}. See exception: {}",
                        new Object[]{ field.getName(), this.getClass().getName(), ExceptionUtils.getStackTrace(
                            exception) });
                }
            }
        }
    }

    @Override
    public final Long getCacheSize(final String cacheVariableName) {
        Long cacheSize = 0L;

        for (final Field field : collectFields(this.getClass())) {
            if ((StringUtils.equals(cacheVariableName, field.getName())) && (field
                .getType() == Cache.class || Cache.class.isAssignableFrom(field.getType()))) {
                try {
                    field.setAccessible(true);
                    final Cache cache = (Cache) field.get(this);
                    cacheSize = cache.size();
                } catch (final Exception exception) {
                    getLogger().error(
                        "An error has occurred while attempting retrieve cache size for {} in the class {}. See exception: {}",
                        new Object[]{ field.getName(), this.getClass().getName(), ExceptionUtils.getStackTrace(
                            exception) });
                }
            }
        }

        return cacheSize;
    }

    @Override
    public final CacheStats getCacheStats(final String cacheVariableName) {
        CacheStats cacheStats = null;

        for (final Field field : collectFields(this.getClass())) {
            if ((StringUtils.equals(cacheVariableName, field.getName())) && (field
                .getType() == Cache.class || Cache.class.isAssignableFrom(field.getType()))) {
                try {
                    field.setAccessible(true);
                    final Cache cache = (Cache) field.get(this);
                    cacheStats = cache.stats();
                } catch (final Exception exception) {
                    getLogger().error(
                        "An error has occurred while attempting retrieve cache statistics for {} in the class {}. See exception: {}",
                        new Object[]{ field.getName(), this.getClass().getName(), ExceptionUtils.getStackTrace(
                            exception) });
                }
            }
        }

        return cacheStats;
    }

    @Override
    public final List<String> listCaches() {
        final ImmutableList.Builder<String> cachesBuilder = new ImmutableList.Builder<String>();

        for (final Field field : collectFields(this.getClass())) {
            if (field.getType() == Cache.class || Cache.class.isAssignableFrom(field.getType())) {
                cachesBuilder.add(field.getName());
            }
        }

        return cachesBuilder.build();
    }

    protected abstract Logger getLogger();

    private static List<Field> collectFields(final Class clazz) {
        final List<Field> fields = Lists.newArrayList();

        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            fields.addAll(collectFields(clazz.getSuperclass()));
        }

        return fields;
    }
}