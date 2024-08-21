package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import org.springframework.cache.Cache;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.RequiredLearningMap;
import uk.gov.cabinetoffice.csl.util.BasicCache;

public class RequiredLearningMapCache extends BasicCache<RequiredLearningMap> {
    public RequiredLearningMapCache(Cache cache) {
        super(cache, "requiredLearningMap", RequiredLearningMap.class);
    }
}
