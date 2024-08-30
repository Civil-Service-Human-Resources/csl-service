package uk.gov.cabinetoffice.csl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.ObjectCache;

import java.time.Duration;

@Configuration
public class RedisCacheConfig {

    @Value("${spring.cache.redis.key-prefix}")
    private String keyPrefix;

    @Value("${learnerRecord.cache.ttlSeconds}")
    private int learnerRecordCacheTTlSeconds;

    @Value("${learningCatalogue.cache.ttlSeconds}")
    private int learningCatalogueCacheTTlSeconds;

    @Value("${csrs.cache.ttlSeconds}")
    private int userCacheTTlSeconds;

    @Bean
    public ObjectCache<Course> courseCatalogueCache(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache("catalogue-course");
        return new ObjectCache<>(cache, Course.class);
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().prefixCacheNameWith(keyPrefix))
                .withCacheConfiguration("course-record",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(learnerRecordCacheTTlSeconds)))
                .withCacheConfiguration("catalogue-course",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(learningCatalogueCacheTTlSeconds)))
                .withCacheConfiguration("user",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(userCacheTTlSeconds)));
    }
}
