package uk.gov.cabinetoffice.csl.config.redis;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import static java.time.Duration.ofSeconds;
import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

@Getter
@Setter
@NoArgsConstructor
public class RedisCacheConfigurationProps {
    private String name;
    private Integer ttl;

    public RedisCacheConfiguration getAsDefaultConfig() {
        return defaultCacheConfig().entryTtl(ofSeconds(ttl));
    }
}
