package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.courseCatalogue.ILearningCatalogueClient;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.CacheGetMultipleOp;
import uk.gov.cabinetoffice.csl.util.ObjectCache;

import java.util.List;

@Service
@Slf4j
public class CourseCacheService {

    private final ObjectCache<Course> cache;
    private final ILearningCatalogueClient client;

    public CourseCacheService(ObjectCache<Course> cache, ILearningCatalogueClient client) {
        this.cache = cache;
        this.client = client;
    }

    public void removeCourseFromCache(String courseId) {
        cache.evict(courseId);
    }

    public Course getCourse(String courseId) {
        return getCourses(List.of(courseId)).stream().findFirst().orElse(null);
    }

    public List<Course> getCourses(List<String> courseIds) {
        try {
            CacheGetMultipleOp<Course> result = cache.getMultiple(courseIds);
            List<Course> courses = result.getCacheHits();
            if (!result.getCacheMisses().isEmpty()) {
                client.getCourses(result.getCacheMisses()).forEach(course -> {
                    courses.add(course);
                    cache.put(course);
                });
            }
            return courses;
        } catch (Cache.ValueRetrievalException ex) {
            log.error("Failed to retrieve courses from cache, falling back to API");
            return client.getCourses(courseIds);
        }
    }

}
