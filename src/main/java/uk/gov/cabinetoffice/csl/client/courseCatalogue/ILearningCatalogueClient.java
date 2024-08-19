package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.util.List;

public interface ILearningCatalogueClient {

    List<Course> getCourses(GetCourseParams params);

    List<Course> getCoursesWithIds(List<String> courseIds);

    Course getCourse(String courseId);

}
