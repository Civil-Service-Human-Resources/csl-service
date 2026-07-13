package uk.gov.cabinetoffice.csl.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.Serializable;

@Slf4j
public class BasicCache<T extends Serializable> {

    private final Cache cache;
    private final String singleId;
    private final Class<T> clazz;

    public BasicCache(Cache cache, String singleId, Class<T> clazz) {
        this.cache = cache;
        this.singleId = singleId;
        this.clazz = clazz;
    }

    public T get() {
        try {
            return cache.get(singleId, clazz);
        } catch (IllegalStateException e) {
            log.warn("IllegalStateException when fetching object from cache");
            evict();
            return null;
        } catch (SerializationException e) {
            log.warn("SerializationException when fetching object from cache");
            evict();
            return null;
        }
    }

    public void put(T object) {
        cache.put(singleId, object);
    }

    public void evict() {
        cache.evict(singleId);
    }

}
