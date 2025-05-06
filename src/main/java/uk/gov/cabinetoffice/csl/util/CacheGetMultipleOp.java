package uk.gov.cabinetoffice.csl.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Data
public class CacheGetMultipleOp<T extends Cacheable> {
    private final List<String> cacheMisses;
    private final List<T> cacheHits;

    public Map<String, T> getCacheHitsAsMap() {
        return this.cacheHits.stream().collect(Collectors.toMap(Cacheable::getId, o -> o));
    }
}
