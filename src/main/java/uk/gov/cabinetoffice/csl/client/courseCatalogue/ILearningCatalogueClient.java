package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import org.springframework.data.domain.Sort;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseSearchResults;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.RequiredLearningMap;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.SearchForCoursesParams;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;

import java.util.Collection;
import java.util.List;

public interface ILearningCatalogueClient {

    List<Course> getCourses(Collection<String> courseIds);

    CourseSearchResults searchForCourses(SearchForCoursesParams params, int page, int size, String sortBy, Sort.Direction sortDirection);

    RequiredLearningMap getRequiredLearningIdMap();

    Event updateEvent(String courseId, String moduleId, Event event);
}
