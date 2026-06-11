package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@Getter
public class LearningCatalogueCacheService {

    private final CourseAudienceMetadataMapCache courseAudienceMetadataMapCache;
    private final LearningTagMapCache learningTagMapCache;
    private final RequiredLearningMapCache requiredLearningMapCache;

    public LearningCatalogueCacheService(CourseAudienceMetadataMapCache courseAudienceMetadataMapCache, LearningTagMapCache learningTagMapCache, RequiredLearningMapCache requiredLearningMapCache) {
        this.courseAudienceMetadataMapCache = courseAudienceMetadataMapCache;
        this.learningTagMapCache = learningTagMapCache;
        this.requiredLearningMapCache = requiredLearningMapCache;
    }

    public void evict() {
        requiredLearningMapCache.evict();
        courseAudienceMetadataMapCache.evict();
    }
}
