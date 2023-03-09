package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.factory.RequestEntityWithBearerAuthFactory;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.invokeService;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.mapJsonStringToObject;

@Slf4j
@Service
public class LearningCatalogueService {

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

    @Cacheable("catalogue-course")
    public Course getCachedCourse(String courseId) {
        ResponseEntity<?> courseResponse = getCourse(courseId);
        if(courseResponse.getStatusCode().is2xxSuccessful()) {
            Course course = mapJsonStringToObject((String)courseResponse.getBody(), Course.class);
            log.debug("Course is retrieved from the Learning-catalogue for the course: {}", course);
            log.info("Course is retrieved from the Learning-catalogue for the course id: {}", courseId);
            return course;
        }
        log.error("Unable to retrieve the course from the Learning-catalogue for the course id: {}",courseId);
        return null;
    }

    @CacheEvict(value = "catalogue-course", allEntries = true)
    public void removeCourseFromCache() {
        log.info("IdentityService.removeServiceTokenFromCache: service token is removed from the cache.");
    }
}
