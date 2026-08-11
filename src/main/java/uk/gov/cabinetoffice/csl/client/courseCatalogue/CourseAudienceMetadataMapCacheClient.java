package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseAudienceMetadataMap;

@Component
public class CourseAudienceMetadataMapCacheClient extends LearningCatalogueCacheFetchClient<CourseAudienceMetadataMap> {
    protected CourseAudienceMetadataMapCacheClient(ILearningCatalogueClient client) {
        super(client);
    }

    @Override
    public CourseAudienceMetadataMap fetch() {
        return client.getAudienceMetadataCourseIds();
    }
}
