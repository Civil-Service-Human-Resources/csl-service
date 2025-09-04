package uk.gov.cabinetoffice.csl.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

@AllArgsConstructor
@Getter
public class CacheGetMultipleResult<T extends Cacheable> {
    private final String id;
    @Nullable
    private final T cacheHit;
}
