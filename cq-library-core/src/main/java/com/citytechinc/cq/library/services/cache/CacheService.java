/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.services.cache;

import com.google.common.cache.CacheStats;

import java.util.List;

public interface CacheService {

    /**
     * Clear all caches.
     */
    void clearAllCaches();

    /**
     * @param cacheVariableName cache name
     */
    void clearSpecificCache(String cacheVariableName);

    /**
     * @param cacheVariableName cache name
     * @return cache size
     */
    Long getCacheSize(String cacheVariableName);

    /**
     * @param cacheVariableName cache name
     * @return cache stats
     */
    CacheStats getCacheStats(String cacheVariableName);

    /**
     * @return list of cache names
     */
    List<String> listCaches();
}