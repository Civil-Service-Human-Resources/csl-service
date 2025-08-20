package uk.gov.cabinetoffice.csl.config.redis;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.RequiredLearningMapCache;
import uk.gov.cabinetoffice.csl.util.ObjectCache;

import java.util.Map;

@Configuration
public class RedisCaches {

    private final RedisCacheConfig redisCacheConfig;

    public RedisCaches(RedisCacheConfig redisCacheConfig) {
        this.redisCacheConfig = redisCacheConfig;
    }

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
    public ObjectCache<ModuleRecord> moduleRecordCache(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache("module-record");
        return new ObjectCache<>(cache, ModuleRecord.class);
    }

    @Bean
    public ObjectCache<LearnerRecord> learnerRecordCache(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache("learner-record");
        return new ObjectCache<>(cache, LearnerRecord.class);
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        Map<String, RedisCacheConfiguration> configs = redisCacheConfig.getAsDefaultConfigMap();
        return (builder) -> builder.withInitialCacheConfigurations(configs);
    }

}
