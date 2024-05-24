package uk.gov.cabinetoffice.csl.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Data
public class CacheGetMultipleOp<T extends Cacheable> {
    private final List<String> cacheMisses;
    private final ArrayList<T> cacheHits;
}
