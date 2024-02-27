package uk.gov.cabinetoffice.csl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

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
