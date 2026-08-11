package uk.gov.cabinetoffice.csl.config.redis;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.NullableModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.util.ModuleRecordCache;
import uk.gov.cabinetoffice.csl.util.ObjectCache;

@Configuration
public class RedisCaches {

    @Bean(name = "learningCatalogue")
    public Cache getLearningCatalogueCache(CacheManager cacheManager) {
        return cacheManager.getCache("catalogue-course");
    }

    @Bean
    public ModuleRecordCache moduleRecordCache(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache("module-record");
        return new ModuleRecordCache(cache, NullableModuleRecord.class);
    }

    @Bean
    public ObjectCache<LearnerRecord> learnerRecordCache(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache("learner-record");
        return new ObjectCache<>(cache, LearnerRecord.class);
    }

    @Bean(name = "organisations")
    public Cache organisationalUnitMapCache(CacheManager cacheManager) {
        return cacheManager.getCache("organisations");
    }


}
