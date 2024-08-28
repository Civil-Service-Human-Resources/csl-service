package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.courseCatalogue.ILearningCatalogueClient;
import uk.gov.cabinetoffice.csl.domain.error.LearningCatalogueResourceNotFoundException;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.*;
import uk.gov.cabinetoffice.csl.util.CacheGetMultipleOp;
import uk.gov.cabinetoffice.csl.util.ObjectCache;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningCatalogueService {

    private final ObjectCache<Course> cache;
    private final RequiredLearningMapCache requiredLearningMapCache;
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
            Course course = getCourses(List.of(courseId)).stream().findFirst().orElse(null);
            if (course == null) {
                throw new LearningCatalogueResourceNotFoundException(String.format("Course '%s'", courseId));
            }
            return course;
        } catch (Exception e) {
            removeCourseFromCache(courseId);
            throw e;
        }
    }

    public List<Course> getCourses(Collection<String> courseIds) {
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

    public List<Course> getRequiredLearningForDepartments(Collection<String> departmentCodes) {
        RequiredLearningMap map = requiredLearningMapCache.get();
        if (map == null) {
            map = client.getRequiredLearningIdMap();
            requiredLearningMapCache.put(map);
        }
        Set<String> uniqueCourseIds = map.getRequiredLearningWithDepartmentCodes(departmentCodes);
        return this.getCourses(uniqueCourseIds);
    }

    public void removeCourseFromCache(String courseId) {
        log.info("LearningCatalogueService.removeCourseFromCache: Catalogue course is removed from the cache for the" +
                " key: {}.", courseId);
        this.cache.evict(courseId);
        RequiredLearningMap map = requiredLearningMapCache.get();
        if (map != null) {
            if (map.doesCourseExistInMap(courseId)) {
                log.info(String.format("Course %s is a required learning course, evicting the required learning map.", courseId));
                requiredLearningMapCache.evict();
            }
        }
    }
}
