package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@Getter
public class LearningCatalogueCacheService {

    private final CourseAudienceMetadataMapCache courseAudienceMetadataMapCache;
    private final RequiredLearningMapCache requiredLearningMapCache;

    public LearningCatalogueCacheService(CourseAudienceMetadataMapCache courseAudienceMetadataMapCache, RequiredLearningMapCache requiredLearningMapCache) {
        this.courseAudienceMetadataMapCache = courseAudienceMetadataMapCache;
        this.requiredLearningMapCache = requiredLearningMapCache;
    }

    public void evict() {
        requiredLearningMapCache.evict();
        courseAudienceMetadataMapCache.evict();
    }
}
