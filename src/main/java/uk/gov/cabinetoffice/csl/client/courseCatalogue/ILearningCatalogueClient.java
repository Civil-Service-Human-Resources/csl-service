package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.util.List;

public interface ILearningCatalogueClient {

    Course getCourse(String courseId);

    List<Course> getCourses(List<String> courseIds);
}
