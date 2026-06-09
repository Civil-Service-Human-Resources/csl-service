package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import org.springframework.cache.Cache;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseAudienceMetadataMap;
import uk.gov.cabinetoffice.csl.util.BasicCache;

public class CourseAudienceMetadataMapCache extends BasicCache<CourseAudienceMetadataMap> {
    public CourseAudienceMetadataMapCache(Cache cache) {
        super(cache, "courseAudienceMetadataMap", CourseAudienceMetadataMap.class);
    }
}
