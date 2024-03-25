package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.courseCatalogue.ILearningCatalogueClient;
import uk.gov.cabinetoffice.csl.domain.error.LearningCatalogueResourceNotFoundException;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningCatalogueService {

    private final ILearningCatalogueClient client;

    public CourseWithModule getCourseWithModule(String courseId, String moduleId) {
        Course course = getCourse(courseId);
        if (course == null) {
            throw new LearningCatalogueResourceNotFoundException(String.format("Module '%s' in course '%s'", moduleId, courseId));
        } else {
            Module module = course.getModule(moduleId);
            if (module != null) {
                return new CourseWithModule(course, module);
            } else {
                throw new LearningCatalogueResourceNotFoundException(String.format("Module '%s' in course '%s'", moduleId, courseId));
            }
        }
    }

    public CourseWithModuleWithEvent getCourseWithModuleWithEvent(String courseId, String moduleId, String eventId) {
        CourseWithModule courseWithModule = getCourseWithModule(courseId, moduleId);
        Event event = courseWithModule.getModule().getEvent(eventId);
        if (event != null) {
            return new CourseWithModuleWithEvent(courseWithModule, event);
        } else {
            throw new LearningCatalogueResourceNotFoundException(String.format("Event '%s' in module '%s' and course '%s'", eventId, moduleId, courseId));
        }
    }


    public Course getCourse(String courseId) {
        try {
            Course course = client.getCourse(courseId);
            if (course == null) {
                throw new LearningCatalogueResourceNotFoundException(String.format("Course '%s'", courseId));
            }
            return course;
        } catch (Exception e) {
            removeCourseFromCache(courseId);
            throw e;
        }
    }

    @CacheEvict(value = "catalogue-course", key = "#courseId")
    public void removeCourseFromCache(String courseId) {
        log.info("LearningCatalogueService.removeCourseFromCache: Catalogue course is removed from the cache for the" +
                " key: {}.", courseId);
    }
}
