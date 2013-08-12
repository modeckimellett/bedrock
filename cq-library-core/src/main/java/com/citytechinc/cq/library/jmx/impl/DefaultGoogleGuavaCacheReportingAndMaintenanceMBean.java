/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.jmx.impl;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;
import com.citytechinc.cq.library.jmx.GoogleGuavaCacheReportingAndMaintenanceMBean;
import com.citytechinc.cq.library.services.cache.CacheService;
import com.google.common.base.Function;
import com.google.common.cache.CacheStats;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.NotCompliantMBeanException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component(immediate = true)
@Property(name = "jmx.objectname", value = "com.citytechinc.cq.library:type=Google Guava Cache Reporting and Maintenance")
@Service
public final class DefaultGoogleGuavaCacheReportingAndMaintenanceMBean extends AnnotatedStandardMBean implements GoogleGuavaCacheReportingAndMaintenanceMBean {

    private static final Logger LOG = LoggerFactory.getLogger(
        DefaultGoogleGuavaCacheReportingAndMaintenanceMBean.class);

    private static final Function<Object, String> REDUCE_LIST_OF_OBJECTS_TO_LIST_OF_CLASS_NAMES = new Function<Object, String>() {

        @Override
        public String apply(final Object object) {
            return object.getClass().getName();
        }
    };

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC,
        referenceInterface = CacheService.class, bind = "bindCacheService", unbind = "unbindCacheService")
    private List<CacheService> cacheServices = Lists.newCopyOnWriteArrayList();

    public DefaultGoogleGuavaCacheReportingAndMaintenanceMBean() throws NotCompliantMBeanException {
        super(GoogleGuavaCacheReportingAndMaintenanceMBean.class);
    }

    @Override
    public void clearAllCaches() {
        for (final CacheService cacheService : cacheServices) {
            cacheService.clearAllCaches();
        }
    }

    @Override
    public void clearAllCachesForService(final String cacheServiceClassName) {
        for (final CacheService cacheService : cacheServices) {
            if (StringUtils.equalsIgnoreCase(cacheService.getClass().getName(), cacheServiceClassName)) {
                cacheService.clearAllCaches();
            }
        }
    }

    @Override
    public void clearSpecificCacheForSpecificService(final String cacheServiceClassName, final String cacheKey) {
        for (final CacheService cacheService : cacheServices) {
            if (StringUtils.equalsIgnoreCase(cacheService.getClass().getName(), cacheServiceClassName)) {
                cacheService.clearSpecificCache(cacheKey);
            }
        }
    }

    @Override
    public TabularDataSupport getCacheStats() {
        TabularDataSupport tabularDataSupport = null;

        try {
            final String[] itemNamesAndDescriptions = { "Cache Service", "Cache Key", "Average Load Penalty", "Eviction Count", "Hit Count", "% Hit Rate", "Load Count", "Load Exception Count", "% Load Exception Rate", "Load Success Count", "Miss Count", "% Miss Rate", "Request Count", "Total Load Time (s)", "Cache Size" };
            final OpenType[] itemTypes = { SimpleType.STRING, SimpleType.STRING, SimpleType.DOUBLE, SimpleType.LONG, SimpleType.LONG, SimpleType.BIGDECIMAL, SimpleType.LONG, SimpleType.LONG, SimpleType.BIGDECIMAL, SimpleType.LONG, SimpleType.LONG, SimpleType.BIGDECIMAL, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG };

            final String[] indexNames = { "Cache Service", "Cache Key" };

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesAndDescriptions,
                itemNamesAndDescriptions, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Caches and Statistics",
                "List of Caches and Statistics", pageType, indexNames);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final CacheService cacheService : cacheServices) {
                final String cacheServiceClassname = cacheService.getClass().getSimpleName();

                for (final String cacheName : cacheService.listCaches()) {
                    final CacheStats cacheStats = cacheService.getCacheStats(cacheName);
                    final Long cacheSize = cacheService.getCacheSize(cacheName);

                    if (cacheStats != null) {
                        final BigDecimal hitRate = new BigDecimal(cacheStats.hitRate()).setScale(2,
                            RoundingMode.HALF_UP).movePointRight(2);
                        final BigDecimal loadExceptionRate = new BigDecimal(cacheStats.loadExceptionRate()).setScale(2,
                            RoundingMode.HALF_UP).movePointRight(2);
                        final BigDecimal missRate = new BigDecimal(cacheStats.missRate()).setScale(2,
                            RoundingMode.HALF_UP).movePointRight(2);
                        final Long loadTimeInSeconds = TimeUnit.SECONDS.convert(cacheStats.totalLoadTime(),
                            TimeUnit.NANOSECONDS);

                        tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesAndDescriptions,
                            new Object[]{ cacheServiceClassname, cacheName, cacheStats.averageLoadPenalty(), cacheStats
                                .evictionCount(), cacheStats.hitCount(), hitRate, cacheStats.loadCount(), cacheStats
                                .loadExceptionCount(), loadExceptionRate, cacheStats.loadSuccessCount(), cacheStats
                                .missCount(), missRate, cacheStats.requestCount(), loadTimeInSeconds, cacheSize }));
                    }
                }
            }
        } catch (final Exception exception) {
            LOG.error("An exception occurred building tabulardata for cache stats.", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getExposedCaches() {
        TabularDataSupport tabularDataSupport = null;

        try {
            final String[] itemNamesAndDescriptions = { "Cache Service", "Cache Key" };
            final OpenType[] itemTypes = { SimpleType.STRING, SimpleType.STRING };

            final String[] indexNames = { "Cache Service", "Cache Key" };

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesAndDescriptions,
                itemNamesAndDescriptions, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Cache Services and Keys",
                "List of Cache Services and Keys", pageType, indexNames);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final CacheService cacheService : cacheServices) {
                final String cacheServiceClassname = cacheService.getClass().getName();

                for (final String cacheName : cacheService.listCaches()) {
                    tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesAndDescriptions,
                        new Object[]{ cacheServiceClassname, cacheName }));
                }
            }
        } catch (final Exception exception) {
            LOG.error("An exception occurred building tabulardata for the exposed caches.", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getRegisteredCacheServices() {
        TabularDataSupport tabularDataSupport = null;

        try {
            final String[] itemNamesAndDescriptions = { "Cache Service" };
            final OpenType[] itemTypes = { SimpleType.STRING };

            final String[] indexNames = { "Cache Service" };

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesAndDescriptions,
                itemNamesAndDescriptions, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Cache Services", "List of Cache Services",
                pageType, indexNames);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final String className : Lists.transform(cacheServices,
                REDUCE_LIST_OF_OBJECTS_TO_LIST_OF_CLASS_NAMES)) {
                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesAndDescriptions,
                    new Object[]{ className }));
            }
        } catch (final Exception exception) {
            LOG.error("An exception occurred building tabulardata for the registered cache services.", exception);
        }

        return tabularDataSupport;
    }

    protected void bindCacheService(final CacheService cacheService) {
        cacheServices.add(cacheService);
    }

    protected void unbindCacheService(final CacheService cacheService) {
        cacheServices.remove(cacheService);
    }
}