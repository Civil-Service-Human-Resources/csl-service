package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import org.springframework.data.domain.Sort;
import uk.gov.cabinetoffice.csl.client.model.BulkUpdateResponse;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCourseUpdateRequest;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCourseUpdateResponse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagDTO;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagStateUpdate;

import java.util.Collection;
import java.util.List;

public interface ILearningCatalogueClient {

    List<Course> getCourses(Collection<String> courseIds);

    CourseSearchResults searchForCourses(SearchForCoursesParams params, int page, int size, String sortBy, Sort.Direction sortDirection);

    RequiredLearningMap getRequiredLearningIdMap();

    CourseAudienceMetadataMap getAudienceMetadataCourseIds();

    Event updateEvent(String courseId, String moduleId, Event event);

    List<LearningTag> getAllLearningTags();

    LearningTag createLearningTag(LearningTagDTO dto);

    LearningTag updateLearningTag(Long id, LearningTagDTO dto);

    BulkUpdateResponse updateLearningTagState(Collection<Long> ids, LearningTagStateUpdate stateUpdate);

    CourseLearningTagSearchResults getCoursesForLearningTag(Long tagId, int page, int size);

    LearningTagCourseUpdateResponse deleteCoursesFromLearningTag(Long tagId, LearningTagCourseUpdateRequest request);
}
