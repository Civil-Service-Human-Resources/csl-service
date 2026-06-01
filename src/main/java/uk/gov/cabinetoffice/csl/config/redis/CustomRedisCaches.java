package uk.gov.cabinetoffice.csl.config.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.TtlObjectCache;

@Configuration
@ConditionalOnProperty(
        prefix = "spring.cache",
        name = {"type"},
        havingValue = "redis",
        matchIfMissing = true
)
public class CustomRedisCaches {

    private final RedisCacheConfig redisCacheConfig;

    public CustomRedisCaches(RedisCacheConfig redisCacheConfig) {
        this.redisCacheConfig = redisCacheConfig;
    }

    @Bean
    public CustomRedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        return new CustomRedisCacheManager(cacheWriter, redisCacheConfig.getDefault(), redisCacheConfig.getAsDefaultConfigMap());
    }

    @Bean
    public TtlObjectCache<Course> courseCatalogueCache(CustomRedisCacheManager cacheManager) {
        CustomRedisCache cache = cacheManager.getCustomRedisCache("catalogue-course");
        return new TtlObjectCache<>(cache, Course.class);
    }

}
