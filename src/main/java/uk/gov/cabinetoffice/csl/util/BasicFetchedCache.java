package uk.gov.cabinetoffice.csl.util;

import org.springframework.cache.Cache;

import java.io.Serializable;

public class BasicFetchedCache<T extends Serializable> extends BasicCache<T> {

    private final IFetchClient<T> fetchClient;

    public BasicFetchedCache(Cache cache, String singleId, Class<T> clazz, IFetchClient<T> fetchClient) {
        super(cache, singleId, clazz);
        this.fetchClient = fetchClient;
    }

    public T get() {
        T object = super.get();
        if (object == null) {
            object = fetchClient.fetch();
            put(object);
        }
        return object;
    }
}
