package uk.gov.cabinetoffice.csl.util;

import java.util.Optional;

public class NullableCacheObject<T extends Cacheable> implements NullableCacheable<T> {

    private final T object;

    public NullableCacheObject(T object) {
        this.object = object;
    }

    @Override
    public Optional<T> getCacheable() {
        return this.object == null ? Optional.empty() : Optional.of(this.object);
    }

    @Override
    public String getCacheableId() {
        return object.toString();
    }
}
