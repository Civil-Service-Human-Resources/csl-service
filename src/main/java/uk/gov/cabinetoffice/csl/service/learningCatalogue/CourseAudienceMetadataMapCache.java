package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.courseCatalogue.CourseAudienceMetadataMapCacheClient;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseAudienceMetadataMap;
import uk.gov.cabinetoffice.csl.util.BasicFetchedCache;

@Component
public class CourseAudienceMetadataMapCache extends BasicFetchedCache<CourseAudienceMetadataMap> {
    public CourseAudienceMetadataMapCache(@Qualifier("learningCatalogue") Cache cache, CourseAudienceMetadataMapCacheClient fetchClient) {
        super(cache, "courseAudienceMetadataMap", CourseAudienceMetadataMap.class, fetchClient);
    }
}
