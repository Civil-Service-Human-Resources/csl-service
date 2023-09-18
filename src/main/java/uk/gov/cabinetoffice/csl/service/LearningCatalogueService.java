package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.courseCatalogue.ILearningCatalogueClient;
import uk.gov.cabinetoffice.csl.domain.error.LearningCatalogueResourceNotFoundException;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

@Slf4j
@Service
public class LearningCatalogueService {

    private final ILearningCatalogueClient client;

    public LearningCatalogueService(ILearningCatalogueClient client) {
        this.client = client;
    }

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
    
    public Course getCourse(String courseId) {
        Course course = client.getCourse(courseId);
        log.info("Course is retrieved from the Learning-catalogue for the course id: {}", courseId);
        log.debug(course.toString());
        return course;
    }

    @Scheduled(cron = "${learningCatalogue.courseCacheEvictionScheduleCron}")
    @CacheEvict(value = "catalogue-course", allEntries = true)
    public void removeAllCoursesFromCache() {
        log.info("LearningCatalogueService.removeAllCoursesFromCache: All catalogue courses are removed from the cache");
    }

    @CacheEvict(value = "catalogue-course", key = "#courseId")
    public void removeCourseFromCache(String courseId) {
        log.info("LearningCatalogueService.removeCourseFromCache: Catalogue course is removed from the cache for the" +
                " key: {}.", courseId);
    }
}
