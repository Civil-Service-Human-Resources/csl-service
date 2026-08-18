package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.courseCatalogue.ILearningCatalogueClient;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCourseAssignmentRequest;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCourseUpdateRequest;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCourseUpdateResponse;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagOverview;
import uk.gov.cabinetoffice.csl.controller.model.CancelEventDto;
import uk.gov.cabinetoffice.csl.domain.error.LearningCatalogueResourceNotFoundException;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.EventStatus;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagDTO;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagStateDTO;
import uk.gov.cabinetoffice.csl.domain.taxonomy.BasicTaxonomyTree;
import uk.gov.cabinetoffice.csl.domain.taxonomy.FormattedTaxonomyItem;
import uk.gov.cabinetoffice.csl.domain.taxonomy.FormattedTaxonomyItems;
import uk.gov.cabinetoffice.csl.util.CacheGetMultipleOp;
import uk.gov.cabinetoffice.csl.util.IUtilService;
import uk.gov.cabinetoffice.csl.util.TtlObjectCache;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningCatalogueService {

    private final IUtilService utilService;
    private final TtlObjectCache<Course> cache;
    private final LearningCatalogueCacheService learningCatalogueCacheService;
    private final LearningTagMapService learningTagMapService;
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

    public <T> Map<String, T> getCourseIdMap(Collection<String> courseIds, Function<Course, T> valueMapper) {
        return getCourses(courseIds).stream().collect(Collectors.toMap(Course::getCacheableId, valueMapper));
    }


    public Map<String, String> getCourseIdToTitleMap(Collection<String> courseIds) {
        return getCourseIdMap(courseIds, Course::getTitle);
    }

    public List<Course> getCourses(Collection<String> courseIds) {
        try {
            CacheGetMultipleOp<Course> result = cache.getMultiple(courseIds);
            List<Course> courses = result.getCacheHits();
            if (!result.getCacheMisses().isEmpty()) {
                client.getCourses(result.getCacheMisses()).forEach(course -> {
                    courses.add(course);
                    cache.put(course, utilService.getDurationUntilTomorrow(ChronoUnit.SECONDS));
                });
            }
            return courses;
        } catch (Cache.ValueRetrievalException ex) {
            log.error("Failed to retrieve courses from cache, falling back to API");
            return client.getCourses(courseIds);
        }
    }

    public CourseAudienceMetadataMap getCourseAudienceMetadataMap() {
        return learningCatalogueCacheService.getCourseAudienceMetadataMapCache().get();
    }

    private RequiredLearningMap getRequiredLearningMap() {
        return learningCatalogueCacheService.getRequiredLearningMapCache().get();
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
        departmentCodes.forEach(departmentCode -> {
            List<String> courseIdsForDep = map.get(departmentCode);
            if (courseIdsForDep != null) {
                courseIdsForDep.forEach(courseId -> {
                    Course course = courseMap.get(courseId);
                    List<Course> courses = result.getOrDefault(departmentCode, new ArrayList<>());
                    courses.add(course);
                    result.put(departmentCode, courses);
                });
            }
        });
        return result;
    }

    public void removeCourseFromCache(String courseId) {
        log.info("LearningCatalogueService.removeCourseFromCache: Catalogue course is removed from the cache for the" +
                " key: {}.", courseId);
        this.cache.evict(courseId);
        learningCatalogueCacheService.evict();
    }

    public void cancelEvent(CourseWithModuleWithEvent data, CancelEventDto cancelEventDto) {
        Course course = data.getCourse();
        Module module = data.getModule();
        Event event = data.getEvent();
        event.setCancellationReason(cancelEventDto.getReason());
        event.setStatus(EventStatus.CANCELLED);
        event = client.updateEvent(course.getCacheableId(), module.getId(), event);
        course.updateEvent(module.getId(), event);
        cache.put(course, utilService.getDurationUntilTomorrow(ChronoUnit.SECONDS));
    }

    public CourseSearchResults searchWithinCourses(Collection<String> allLearningPlanCourseIds, String q, int page, int size, Sort.Direction sort) {
        SearchForCoursesParams p = SearchForCoursesParams.builder()
                .query(q)
                .status(List.of(CourseStatus.PUBLISHED, CourseStatus.ARCHIVED))
                .courseIds(allLearningPlanCourseIds)
                .build();
        return client.searchForCourses(p, page, size, "title", sort);
    }

    public CourseSearchResults getCoursesForLetter(String startsWith, Pageable pageableParams) {
        SearchForCoursesParams p = SearchForCoursesParams.builder().titleStartsWith(startsWith).build();
        return client.searchForCourses(p, pageableParams.getPageNumber(), pageableParams.getPageSize(), "title", Sort.Direction.ASC);
    }

    public Map<String, Course> getCourseIdToCourseMap(List<String> courseIds) {
        return getCourseIdMap(courseIds, course -> course);
    }

    public BasicTaxonomyTree getLearningTagTree() {
        return learningTagMapService.getTree();
    }

    public LearningTagOverview getLearningTagOverview(Long learningTagId) {
        return learningTagMapService.getOverview(learningTagId);
    }

    public LearningTagOverview createLearningTag(LearningTagDTO dto) {
        return learningTagMapService.create(dto);
    }

    public FormattedTaxonomyItems<FormattedTaxonomyItem> getFormattedLearningTagNames() {
        return learningTagMapService.getFormattedNames();
    }

    public LearningTagOverview patchLearningTag(Long learningTagId, LearningTagDTO dto) {
        return learningTagMapService.update(learningTagId, dto);
    }

    public LearningTagOverview updateState(Long learningTagId, LearningTagStateDTO request) {
        return learningTagMapService.updateState(learningTagId, request.getState());
    }

    public CourseLearningTagSearchResults getCoursesForLearningTag(Long tagId, int page, int size) {
        return client.getCoursesForLearningTag(tagId, page, size);
    }

    public HyperlinkSearchResults getHyperlinksForLearningTag(Long tagId, int page, int size) {
        return client.getHyperlinksForLearningTag(tagId, page, size);
    }

    public LearningTagCourseUpdateResponse deleteCoursesFromLearningTag(Long tagId, LearningTagCourseUpdateRequest request) {
        return client.deleteCoursesFromLearningTag(tagId, request);
    }

    public LearningTagCourseUpdateResponse assignCoursesToLearningTags(LearningTagCourseAssignmentRequest request) {
        return client.assignCoursesToLearningTags(request);
    }
}
