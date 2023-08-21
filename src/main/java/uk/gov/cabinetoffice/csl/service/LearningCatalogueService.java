package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.courseCatalogue.ILearningCatalogueClient;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

@Slf4j
@Service
public class LearningCatalogueService {

    private final ILearningCatalogueClient client;

    public LearningCatalogueService(ILearningCatalogueClient client) {
        this.client = client;
    }

    @Cacheable(value = "catalogue-course", key = "#courseId")
    public Course getCourse(String courseId) {
        try {
            Course course = client.getCourse(courseId);
            log.info("Course is retrieved from the Learning-catalogue for the course id: {}", courseId);
            return course;
        } catch (Exception e) {
            log.error("Unable to retrieve the course from the Learning-catalogue for the course id: {}. Exception: {}", courseId, e.getMessage());
            return null;
        }
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
