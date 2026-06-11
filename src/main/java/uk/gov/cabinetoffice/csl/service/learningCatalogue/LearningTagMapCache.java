package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.courseCatalogue.LearningTagMapCacheClient;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagMap;
import uk.gov.cabinetoffice.csl.util.BasicFetchedCache;

@Component
public class LearningTagMapCache extends BasicFetchedCache<LearningTagMap> {

    public LearningTagMapCache(@Qualifier("learningCatalogue") Cache cache, LearningTagMapCacheClient client) {
        super(cache, "learningTagMap", LearningTagMap.class, client);
    }

}
