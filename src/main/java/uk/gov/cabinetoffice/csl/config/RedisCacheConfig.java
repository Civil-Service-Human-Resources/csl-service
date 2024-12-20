package uk.gov.cabinetoffice.csl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.RequiredLearningMapCache;
import uk.gov.cabinetoffice.csl.util.ObjectCache;

import java.time.Duration;

@Configuration
public class RedisCacheConfig {

    @Value("${learnerRecord.cache.ttlSeconds}")
    private int learnerRecordCacheTTlSeconds;

    @Value("${learningCatalogue.cache.ttlSeconds}")
    private int learningCatalogueCacheTTlSeconds;

    @Value("${csrs.cache.ttlSeconds}")
    private int userCacheTTlSeconds;

    @Bean
    public RequiredLearningMapCache requiredLearningMapCache(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache("catalogue-course");
        return new RequiredLearningMapCache(cache);
    }

    @Bean
    public ObjectCache<Course> courseCatalogueCache(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache("catalogue-course");
        return new ObjectCache<>(cache, Course.class);
    }

    @Bean
    public ObjectCache<CourseRecord> courseRecordCache(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache("course-record");
        return new ObjectCache<>(cache, CourseRecord.class);
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("course-record",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(learnerRecordCacheTTlSeconds)))
                .withCacheConfiguration("catalogue-course",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(learningCatalogueCacheTTlSeconds)))
                .withCacheConfiguration("user",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(userCacheTTlSeconds)));
    }
}
