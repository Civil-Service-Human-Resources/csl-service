package uk.gov.cabinetoffice.csl.config.redis;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.time.Duration;

public class CustomRedisCache extends RedisCache implements ITtlCache {

    private final RedisCacheWriter cacheWriter;

    protected CustomRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfiguration) {
        super(name, cacheWriter, cacheConfiguration);
        this.cacheWriter = cacheWriter;
    }

    public void put(Object key, Object value, Long ttlSeconds) {
        byte[] binaryKey = serializeCacheKey(createCacheKey(key));
        byte[] binaryValue = serializeCacheValue(preProcessCacheValue(value));
        Duration timeToLive = Duration.ofSeconds(ttlSeconds);
        cacheWriter.put(getName(), binaryKey, binaryValue, timeToLive);
    }
}
