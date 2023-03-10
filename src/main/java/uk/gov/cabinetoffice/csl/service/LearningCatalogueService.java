package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.factory.RequestEntityWithBearerAuthFactory;
import javax.management.timer.Timer;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.invokeService;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.mapJsonStringToObject;

@Slf4j
@Service
public class LearningCatalogueService {

    private static final long COURSE_CACHE_EVICTION_SCHEDULE = Timer.ONE_DAY;

    private final RequestEntityWithBearerAuthFactory requestEntityFactory;

    @Value("${learningCatalogue.courseUrl}")
    private String courseUrl;

    public LearningCatalogueService(RequestEntityWithBearerAuthFactory requestEntityFactory) {
        this.requestEntityFactory = requestEntityFactory;
    }

    public ResponseEntity<?> getCourse(String courseId) {
        log.info("LearningCatalogueService.getCourse: Invoking Learning-catalogue service to retrieve course.");
        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createGetRequestWithBearerAuth(
                String.format(courseUrl, courseId), null);
        return invokeService(requestWithBearerAuth);
    }

    @Cacheable(value="catalogue-courses", key="#courseId")
    public Course getCachedCourse(String courseId) {
        ResponseEntity<?> courseResponse = getCourse(courseId);
        if(courseResponse.getStatusCode().is2xxSuccessful()) {
            Course course = mapJsonStringToObject((String)courseResponse.getBody(), Course.class);
            log.info("Course is retrieved from the Learning-catalogue for the course id: {}", courseId);
            return course;
        }
        log.error("Unable to retrieve the course from the Learning-catalogue for the course id: {}",courseId);
        return null;
    }

    @Scheduled(fixedRate = COURSE_CACHE_EVICTION_SCHEDULE)
    @CacheEvict(value = "catalogue-courses", allEntries = true)
    public void removeAllCoursesFromCache() {
        log.info("LearningCatalogueService.removeAllCoursesFromCache: All catalogue courses are removed from the" +
                " cache after every {} seconds.", COURSE_CACHE_EVICTION_SCHEDULE);
    }

    @CacheEvict(value = "catalogue-courses", key="#courseId")
    public void removeCourseFromCache(String courseId) {
        log.info("LearningCatalogueService.removeCourseFromCache: Catalogue course is removed from the cache for the" +
                " key: {}.", courseId);
    }
}
