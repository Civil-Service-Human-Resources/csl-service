package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

public interface ILearningCatalogueClient {

    Course getCourse(String courseId);
    
}
