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
        List<CacheGetMultipleResult<T>> results = ids.parallelStream().map(id -> {
            T nullableCacheEntry = get(id);
            if (nullableCacheEntry == null) {
                put(createNullCacheObject(id));
            }
            return new CacheGetMultipleResult<>(id, nullableCacheEntry);
        }).toList();
        List<String> missingIds = new ArrayList<>();
        List<R> hits = new ArrayList<>();
        results.forEach(r -> {
            if (r.getCacheHit() == null) {
                missingIds.add(r.getId());
            } else if (r.getCacheHit().getCacheable().isPresent()) {
                hits.add(r.getCacheHit().getCacheable().get());
            }
        });
        log.debug("{} cache getMultiple cache misses: {}", getCacheName(), String.join(", ", missingIds));
        return new CacheGetMultipleOp<>(missingIds, hits);
    }

}
