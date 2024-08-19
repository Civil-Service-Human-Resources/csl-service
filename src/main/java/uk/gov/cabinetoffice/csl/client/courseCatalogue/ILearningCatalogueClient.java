package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.util.List;

public interface ILearningCatalogueClient {

    List<Course> getCourses(List<String> courseIds);

    List<Course> getPagedCourses(GetPagedCourseParams params);

    Course getCourse(String courseId);

}
