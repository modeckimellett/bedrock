/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.services;

import com.google.common.cache.CacheStats;

import java.util.List;

public interface CacheService {

    /**
     *
     */
    void clearAllCaches();

    /**
     * @param cacheVariableName
     */
    void clearSpecificCache(String cacheVariableName);

    /**
     * @return
     */
    List<String> listCaches();

    /**
     * @param cacheVariableName
     * @return
     */
    CacheStats getCacheStats(String cacheVariableName);

    /**
     * @param cacheVariableName
     * @return
     */
    Long getCacheSize(String cacheVariableName);
}