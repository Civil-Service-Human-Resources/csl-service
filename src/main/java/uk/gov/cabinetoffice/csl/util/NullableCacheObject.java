package uk.gov.cabinetoffice.csl.util;

import java.util.Optional;

public class NullableCacheObject<T extends Cacheable> implements NullableCacheable<T> {

    private final String cacheableId;
    private final T object;

    public NullableCacheObject(String cacheableId, T object) {
        this.cacheableId = cacheableId;
        this.object = object;
    }

    @Override
    public Optional<T> getCacheable() {
        return this.object == null ? Optional.empty() : Optional.of(this.object);
    }

    @Override
    public String getCacheableId() {
        return cacheableId;
    }
}
