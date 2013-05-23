/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.jmx;

import com.adobe.granite.jmx.annotation.Description;
import com.adobe.granite.jmx.annotation.Name;

import javax.management.openmbean.TabularDataSupport;

@Description("Google Guava Cache Reporting and Maintenance")
public interface GoogleGuavaCacheReportingAndMaintenanceMBean {

    /**
     *
     */
    @Description("Clear all guava caches within all reporting caching services")
    void clearAllCaches();

    /**
     * @return
     */
    @Description("Lists all registered cache services")
    TabularDataSupport getRegisteredCacheServices();

    /**
     * @param cacheService
     */
    @Description("Clear all guava caches within a specific cache service")
    void clearAllCachesForService(@Name("cacheService") @Description("The fully qualified path of a cache service listed in the Registered Cache Services") String cacheService);

    /**
     * @param cacheService
     * @param cacheKey
     */
    @Description("Clear a specific guava cache within a specific cache service")
    void clearSpecificCacheForSpecificService(@Name("cacheService") @Description("The fully qualified path of a cache service listed in the Registered Cache Services") String cacheService, @Name("cacheKey") @Description("The cache key listed in the exposed caches") String cacheKey);

    /**
     * @return
     */
    @Description("Lists all caches exposed by all reporting cache services")
    TabularDataSupport getExposedCaches();

    /**
     * @return
     */
    @Description("Lists all cache statistics for all caches exposed by all reporting cache services")
    TabularDataSupport getCacheStats();

}