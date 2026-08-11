package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.courseCatalogue.RequiredLearningMapCacheClient;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.RequiredLearningMap;
import uk.gov.cabinetoffice.csl.util.BasicFetchedCache;

@Component
public class RequiredLearningMapCache extends BasicFetchedCache<RequiredLearningMap> {
    public RequiredLearningMapCache(@Qualifier("learningCatalogue") Cache cache, RequiredLearningMapCacheClient client) {
        super(cache, "requiredLearningMap", RequiredLearningMap.class, client);
    }
}
