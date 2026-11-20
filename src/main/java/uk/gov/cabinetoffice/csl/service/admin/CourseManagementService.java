package uk.gov.cabinetoffice.csl.service.admin;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.admin.model.AdminCourseOverview;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

@Service
public class CourseManagementService {

    private final LearningCatalogueService learningCatalogueService;
    private final AdminManagementFactory factory;

    public CourseManagementService(LearningCatalogueService learningCatalogueService, AdminManagementFactory factory) {
        this.learningCatalogueService = learningCatalogueService;
        this.factory = factory;
    }

    public AdminCourseOverview getOverview(String courseId) {
        Course course = learningCatalogueService.getCourse(courseId);
        return factory.createCourseOverview(course);
    }
}
