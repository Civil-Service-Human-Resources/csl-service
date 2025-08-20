package uk.gov.cabinetoffice.csl.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Specialised cache for storing objects that can be null, but are also completely controlled by csl-service.
 *
 * @param <T>
 */
@Slf4j
public abstract class NullableObjectCache<R extends Cacheable, T extends NullableCacheObject<R>> extends ObjectCache<T> {

    public NullableObjectCache(Cache cache, Class<T> clazz) {
        super(cache, clazz);
    }

    T createNullCacheObject(String id) {
        return createCacheObject(id, null);
    }

    abstract T createCacheObject(String id, R value);

    public void putValue(R value) {
        put(createCacheObject(value.getCacheableId(), value));
    }

    public CacheGetMultipleOp<R> getMultipleValues(Collection<String> ids) {
        List<String> missingIds = new ArrayList<>();
        ArrayList<R> hits = new ArrayList<>();
        ids.parallelStream().forEach(id -> {
            T object = get(id);
            if (object == null) {
                missingIds.add(id);
                put(createNullCacheObject(id));
            } else {
                object.getCacheable().ifPresent(hits::add);
            }
        });
        log.debug("{} cache getMultiple cache misses: {}", getCacheName(), String.join(", ", missingIds));
        return new CacheGetMultipleOp<>(missingIds, hits);
    }

}
