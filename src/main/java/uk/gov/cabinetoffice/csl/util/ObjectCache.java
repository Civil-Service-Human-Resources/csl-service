package uk.gov.cabinetoffice.csl.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Slf4j
public class ObjectCache<T extends Cacheable> {

    private final Cache cache;
    private final Class<T> clazz;

    public ObjectCache(Cache cache, Class<T> clazz) {
        this.cache = cache;
        this.clazz = clazz;
    }

    public T get(String id) {
        log.debug("{} cache get object with ID : {}", cache.getName(), id);
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
        log.debug("{} cache getMultiple cache misses: {}", cache.getName(), String.join(", ", missingIds));
        return new CacheGetMultipleOp<>(missingIds, hits);
    }

    public void put(T object) {
        log.debug("{} cache put object: {} with id {}", cache.getName(), object.getCacheableId(), object);
        cache.put(object.getCacheableId(), object);
    }

    public void evict(String id) {
        cache.evict(id);
    }

}
