package uk.gov.cabinetoffice.csl.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.config.redis.ITtlCache;

@Slf4j
@Getter
public class TtlObjectCache<T extends Cacheable> extends ObjectCache<T> {

    private final ITtlCache cache;

    public TtlObjectCache(ITtlCache cache, Class<T> clazz) {
        super(cache, clazz);
        this.cache = cache;
    }

    public void put(T object, Long ttlSeconds) {
        log.debug("{} cache put object: {} with id {}. TTL: {}", getCacheName(), object.getCacheableId(), object, ttlSeconds);
        cache.put(object.getCacheableId(), object, ttlSeconds);
    }
}
