package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.RequiredLearningMap;

import java.util.Collection;
import java.util.List;

public interface ILearningCatalogueClient {

    List<Course> getCourses(Collection<String> courseIds);

    Course getCourse(String courseId);

    RequiredLearningMap getRequiredLearningIdMap();

}
