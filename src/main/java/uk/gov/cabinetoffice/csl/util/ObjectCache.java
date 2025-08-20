package uk.gov.cabinetoffice.csl.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Getter
public class ObjectCache<T extends Cacheable> {

    private final Cache cache;
    private final Class<T> clazz;

    public T get(String id) {
        log.debug("{} cache get object with ID : {}", getCacheName(), id);
        return cache.get(id, clazz);
    }

    public CacheGetMultipleOp<T> getMultiple(Collection<String> ids) {
        List<String> missingIds = new ArrayList<>();
        ArrayList<T> hits = new ArrayList<>();
        ids.parallelStream().forEach(id -> {
            T object = get(id);
            if (object == null) {
                missingIds.add(id);
            } else {
                hits.add(object);
            }
        });
        log.debug("{} cache getMultiple cache misses: {}", getCacheName(), String.join(", ", missingIds));
        return new CacheGetMultipleOp<>(missingIds, hits);
    }

    public void put(T object) {
        log.debug("{} cache put object: {} with id {}", getCacheName(), object.getCacheableId(), object);
        cache.put(object.getCacheableId(), object);
    }

    public void evict(String id) {
        cache.evict(id);
    }

    public String getCacheName() {
        return cache.getName();
    }

}
