package uk.gov.cabinetoffice.csl.config.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "spring.cache.redis")
@RequiredArgsConstructor
@Getter
public class RedisCacheConfig {

    private final String keyPrefix;
    private final Map<String, RedisCacheConfigurationProps> caches;

}
