package uk.gov.cabinetoffice.csl.util;

import org.springframework.cache.Cache;

import java.io.Serializable;

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
        return cache.get(singleId, clazz);
    }

    public void put(T object) {
        cache.put(singleId, object);
    }

    public void evict() {
        cache.evict(singleId);
    }

}
