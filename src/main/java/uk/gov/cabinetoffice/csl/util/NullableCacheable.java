package uk.gov.cabinetoffice.csl.util;

import java.util.Optional;

public interface NullableCacheable<T extends Cacheable> extends Cacheable {
    Optional<T> getCacheable();
}
