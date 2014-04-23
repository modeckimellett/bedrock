package com.citytechinc.aem.bedrock.core.services.cache

import com.google.common.cache.Cache
import com.google.common.cache.CacheStats
import com.google.common.cache.LoadingCache
import org.slf4j.Logger
import spock.lang.Specification

class AbstractCacheServiceSpec extends Specification {

    class TestCacheService extends AbstractCacheService {

        Cache johnny

        LoadingCache june

        @Override
        protected Logger getLogger() {
            [error: {}] as Logger
        }
    }

    def "clear all caches"() {
        setup:
        def cacheService = new TestCacheService()
        def johnny = Mock(Cache)
        def june = Mock(LoadingCache)

        cacheService.johnny = johnny
        cacheService.june = june

        when:
        cacheService.clearAllCaches()

        then:
        1 * johnny.invalidateAll()
        1 * june.invalidateAll()
    }

    def "clear specific cache"() {
        setup:
        def cacheService = new TestCacheService()
        def johnny = Mock(Cache)
        def june = Mock(LoadingCache)

        cacheService.johnny = johnny
        cacheService.june = june

        when:
        cacheService.clearSpecificCache("june")

        then:
        0 * johnny.invalidateAll()
        1 * june.invalidateAll()
    }

    def "get cache size"() {
        setup:
        def cacheService = new TestCacheService()
        def johnny = Mock(Cache) {
            size() >> Long.MAX_VALUE
        }

        cacheService.johnny = johnny

        expect:
        cacheService.getCacheSize("johnny") == Long.MAX_VALUE
    }

    def "get cache stats"() {
        setup:
        def cacheService = new TestCacheService()
        def johnny = Mock(Cache) {
            stats() >> new CacheStats(Long.MAX_VALUE, 0, 0, 0, 0, 0)
        }

        cacheService.johnny = johnny

        expect:
        cacheService.getCacheStats("johnny").hitCount() == Long.MAX_VALUE
    }

    def "list caches"() {
        setup:
        def cacheService = new TestCacheService()

        expect:
        cacheService.listCaches() == ["johnny", "june"]
    }
}