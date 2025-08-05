package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.courseCatalogue.ILearningCatalogueClient;
import uk.gov.cabinetoffice.csl.controller.model.CancelEventDto;
import uk.gov.cabinetoffice.csl.domain.error.LearningCatalogueResourceNotFoundException;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.EventStatus;
import uk.gov.cabinetoffice.csl.util.CacheGetMultipleOp;
import uk.gov.cabinetoffice.csl.util.ObjectCache;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public Map<String, String> getCourseIdToTitleMap(Collection<String> courseIds) {
        return getCourses(courseIds).stream().collect(Collectors.toMap(Course::getCacheableId, Course::getTitle));
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

    private RequiredLearningMap getRequiredLearningMap() {
        RequiredLearningMap map = requiredLearningMapCache.get();
        if (map == null) {
            map = client.getRequiredLearningIdMap();
            requiredLearningMapCache.put(map);
        }
        return map;
    }

    public List<String> getRequiredLearningIdsForDepartments(Collection<String> departmentCodes) {
        return getRequiredLearningMap().getRequiredLearningWithDepartmentCodes(departmentCodes).stream().toList();
    }

    public List<Course> getRequiredLearningForDepartments(Collection<String> departmentCodes) {
        return this.getCourses(getRequiredLearningIdsForDepartments(departmentCodes));
    }

    public Map<String, List<Course>> getRequiredLearningForDepartmentsMap(Collection<String> departmentCodes) {
        Map<String, ArrayList<String>> map = getRequiredLearningMap().getPartialMap(departmentCodes);
        Map<String, List<Course>> result = new HashMap<>();
        Map<String, Course> courseMap = getCourses(
                new HashSet<>(map.entrySet().stream().flatMap(entry -> entry.getValue().stream()).collect(Collectors.toSet()))
        ).stream().collect(Collectors.toMap(Course::getId, Function.identity()));
        departmentCodes.forEach(departmentCode -> map.get(departmentCode).forEach(courseId -> {
            Course course = courseMap.get(courseId);
            List<Course> courses = result.getOrDefault(departmentCode, new ArrayList<>());
            courses.add(course);
            result.put(departmentCode, courses);
        }));
        return result;
    }

    public void removeCourseFromCache(String courseId) {
        log.info("LearningCatalogueService.removeCourseFromCache: Catalogue course is removed from the cache for the" +
                " key: {}.", courseId);
        this.cache.evict(courseId);
        requiredLearningMapCache.evict();
    }

    public void cancelEvent(CourseWithModuleWithEvent data, CancelEventDto cancelEventDto) {
        Course course = data.getCourse();
        Module module = data.getModule();
        Event event = data.getEvent();
        event.setCancellationReason(cancelEventDto.getReason());
        event.setStatus(EventStatus.CANCELLED);
        event = client.updateEvent(course.getCacheableId(), module.getId(), event);
        course.updateEvent(module.getId(), event);
        cache.put(course);
    }
}
