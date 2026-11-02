package uk.gov.cabinetoffice.csl.config.redis;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.lang.Nullable;

import static java.time.Duration.ofSeconds;
import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

@Getter
@Setter
@NoArgsConstructor
public class RedisCacheConfigurationProps {
    private String name;
    @Nullable
    private Integer ttl;

    public RedisCacheConfiguration getAsDefaultConfig() {
        RedisCacheConfiguration redisCacheConfiguration = defaultCacheConfig();
        if (ttl != null) {
            redisCacheConfiguration.entryTtl(ofSeconds(ttl));
        }
        return redisCacheConfiguration;
    }
}
