package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.model.BulkUpdateResponse;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCourseAssignmentRequest;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagUpdateRequest;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagUpdateResponse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseLearningTagSearchResults;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.HyperlinkSearchResults;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagDTO;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagMap;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagStateUpdate;

import java.util.Collection;

@Service
public class LearningTagMapClient implements ILearningTagMapClient {

    private final ILearningCatalogueClient learningCatalogueClient;

    public LearningTagMapClient(ILearningCatalogueClient learningCatalogueClient) {
        this.learningCatalogueClient = learningCatalogueClient;
    }

    @Override
    public LearningTag create(LearningTagDTO dto) {
        return learningCatalogueClient.createLearningTag(dto);
    }

    @Override
    public LearningTag patch(Long id, LearningTagDTO dto) {
        return learningCatalogueClient.updateLearningTag(id, dto);
    }

    @Override
    public LearningTagMap fetch() {
        return LearningTagMap.buildFromList(learningCatalogueClient.getAllLearningTags());
    }

    public BulkUpdateResponse updateState(Collection<Long> ids, LearningTagStateUpdate stateUpdate) {
        return learningCatalogueClient.updateLearningTagState(ids, stateUpdate);
    }

    public LearningTagUpdateResponse removeCourses(Long tagId, LearningTagUpdateRequest request) {
        return learningCatalogueClient.deleteCoursesFromLearningTag(tagId, request);
    }

    public LearningTagUpdateResponse addCourses(LearningTagCourseAssignmentRequest request) {
        return learningCatalogueClient.assignCoursesToLearningTags(request);
    }

    public CourseLearningTagSearchResults getCourses(Long tagId, int page, int size) {
        return learningCatalogueClient.getCoursesForLearningTag(tagId, page, size);
    }

    public LearningTagUpdateResponse removeHyperlinks(Long tagId, LearningTagUpdateRequest request) {
        return learningCatalogueClient.deleteHyperlinksFromLearningTag(tagId, request);
    }

    public HyperlinkSearchResults getHyperlinks(Long tagId, int page, int size) {
        return learningCatalogueClient.getHyperlinksForLearningTag(tagId, page, size);
    }
}
