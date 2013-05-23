/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Responsible for exposing cache stats and cache variables in CacheServices.
 */
public abstract class AbstractCacheService implements CacheService {

    protected abstract Logger getLogger();

    @Override
    public void clearAllCaches() {

        for (final Field field : collectFields(this.getClass())) {

            if (field.getType() == Cache.class || Cache.class.isAssignableFrom(field.getType())) {

                try {

                    field.setAccessible(true);
                    final Cache cache = (Cache) field.get(this);
                    cache.invalidateAll();

                } catch (final Exception exception) {

                    getLogger().error("An error has occurred while attempting to invalidate cache values for {} in the class {}. See exception: {}",
                            new Object[] {field.getName(), this.getClass().getName(), ExceptionUtils.getStackTrace(exception)});
                }
            }
        }
    }

    @Override
    public void clearSpecificCache(String cacheVariableName) {

        for (final Field field : collectFields(this.getClass())) {

            if ((StringUtils.equals(cacheVariableName, field.getName())) && (field.getType() == Cache.class || Cache.class.isAssignableFrom(field.getType()))) {

                try {

                    field.setAccessible(true);
                    final Cache cache = (Cache) field.get(this);
                    cache.invalidateAll();

                } catch (final Exception exception) {

                    getLogger().error("An error has occurred while attempting to invalidate cache values for {} in the class {}. See exception: {}",
                            new Object[] {field.getName(), this.getClass().getName(), ExceptionUtils.getStackTrace(exception)});
                }
            }
        }
    }

    @Override
    public List<String> listCaches() {

        final ImmutableList.Builder<String> cachesBuilder = new ImmutableList.Builder<String>();

        for (final Field field : collectFields(this.getClass())) {

            if (field.getType() == Cache.class || Cache.class.isAssignableFrom(field.getType())) {

                cachesBuilder.add(field.getName());
            }
        }

        return cachesBuilder.build();
    }

    @Override
    public CacheStats getCacheStats(String cacheVariableName) {
        CacheStats cacheStats = null;

        for (final Field field : collectFields(this.getClass())) {

            if ((StringUtils.equals(cacheVariableName, field.getName())) && (field.getType() == Cache.class || Cache.class.isAssignableFrom(field.getType()))) {

                try {

                    field.setAccessible(true);
                    final Cache cache = (Cache) field.get(this);
                    cacheStats = cache.stats();

                } catch (final Exception exception) {

                    getLogger().error("An error has occurred while attempting retrieve cache statistics for {} in the class {}. See exception: {}",
                            new Object[] {field.getName(), this.getClass().getName(), ExceptionUtils.getStackTrace(exception)});
                }
            }
        }

        return cacheStats;
    }

    @Override
    public Long getCacheSize(final String cacheVariableName) {
        Long cacheSize = 0L;

        for (final Field field : collectFields(this.getClass())) {

            if ((StringUtils.equals(cacheVariableName, field.getName())) && (field.getType() == Cache.class || Cache.class.isAssignableFrom(field.getType()))) {

                try {

                    field.setAccessible(true);
                    final Cache cache = (Cache) field.get(this);
                    cacheSize = cache.size();

                } catch (final Exception exception) {

                    getLogger().error("An error has occurred while attempting retrieve cache size for {} in the class {}. See exception: {}",
                            new Object[] {field.getName(), this.getClass().getName(), ExceptionUtils.getStackTrace(exception)});
                }
            }
        }

        return cacheSize;
    }

    private List<Field> collectFields(Class clazz){
    	List<Field> fields=new ArrayList<Field>();
    	if(clazz!=null){
    		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
    		fields.addAll(collectFields(clazz.getSuperclass()));
    	}
    	return fields;
    }
}