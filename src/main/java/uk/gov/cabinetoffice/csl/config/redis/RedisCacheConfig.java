package uk.gov.cabinetoffice.csl.config.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

@ConfigurationProperties(prefix = "spring.cache.redis")
@RequiredArgsConstructor
@Getter
public class RedisCacheConfig {

    private final String keyPrefix;
    private final Integer defaultTtl;
    private final Map<String, RedisCacheConfigurationProps> caches;

    public RedisCacheConfiguration getDefault() {
        return defaultCacheConfig();
    }

    public Map<String, RedisCacheConfiguration> getAsDefaultConfigMap() {
        return caches
                .keySet().stream().collect(Collectors.toMap(k -> k, k -> caches.get(k).getAsDefaultConfig().prefixCacheNameWith(getKeyPrefix())));
    }

}
