package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.client.model.BulkUpdateResponse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.CourseLearningTagDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagDTO;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagsPagedResponse;
import uk.gov.cabinetoffice.csl.util.IUtilService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LearningCatalogueClient implements ILearningCatalogueClient {

    private final LearningCatalogueConfiguration config;
    private final IHttpClient httpClient;
    private final CourseFactory courseFactory;
    private final IUtilService utilService;

    public LearningCatalogueClient(LearningCatalogueConfiguration learningCatalogueConfiguration, @Qualifier("learningCatalogueHttpClient") IHttpClient httpClient,
                                   CourseFactory courseFactory, IUtilService utilService) {
        this.config = learningCatalogueConfiguration;
        this.httpClient = httpClient;
        this.courseFactory = courseFactory;
        this.utilService = utilService;
    }

    @Override
    public List<Course> getCourses(Collection<String> courseIds) {
        log.info("Getting courses with ids '{}' from learning catalogue API", courseIds);
        return utilService.batchList(courseIds.stream().toList(), config.getCourseBatchSize())
                .stream().flatMap(courseIdsBatch -> {
                    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(config.getCourseUrl());
                    uriBuilder.queryParam("courseId", courseIdsBatch);
                    RequestEntity<Void> request = RequestEntity.get(uriBuilder.build().toUriString()).build();
                    List<Course> courses = httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
                    });
                    return courses.stream();
                }).map(this::buildCourseData).collect(Collectors.toList());
    }

    @Override
    public CourseSearchResults searchForCourses(SearchForCoursesParams params, int page, int size, String sortBy, Sort.Direction sortDirection) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(config.getCourseV2SearchUrl());
        uriBuilder.replaceQueryParam("size", size).replaceQueryParam("page", page)
                .replaceQueryParam("sort.field", sortBy).replaceQueryParam("sort.direction", sortDirection.name());
        RequestEntity<SearchForCoursesParams> request = RequestEntity.post(uriBuilder.toUriString()).body(params);
        CourseSearchResults resp = httpClient.executeRequest(request, CourseSearchResults.class);
        resp.getResults().forEach(this::buildCourseData);
        return resp;
    }

    @Override
    public RequiredLearningMap getRequiredLearningIdMap() {
        String url = String.format("%s/required-learning-map", config.getCourseV2Url());
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeRequest(request, RequiredLearningMap.class);
    }

    @Override
    public CourseAudienceMetadataMap getAudienceMetadataCourseIds() {
        String url = String.format("%s/audience-attribute-map", config.getCourseV2Url());
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeRequest(request, CourseAudienceMetadataMap.class);
    }

    @Override
    public Event updateEvent(String courseId, String moduleId, Event event) {
        String url = String.format("%s/%s/modules/%s/events/%s", config.getCourseUrl(), courseId, moduleId, event.getId());
        RequestEntity<Event> request = RequestEntity.put(url).body(event);
        return httpClient.executeRequest(request, Event.class);
    }

    private Course buildCourseData(Course course) {
        Map<String, Integer> departmentCodeToRequiredAudienceMap = courseFactory.buildRequiredLearningDepartmentMap(course.getAudiences());
        course.setDepartmentCodeToRequiredAudienceMap(departmentCodeToRequiredAudienceMap);

        List<String> moduleIdsRequiredForCompletion = courseFactory.getRequiredModulesForCompletion(course.getModules());
        course.getModules().forEach(m -> {
            if (moduleIdsRequiredForCompletion.contains(m.getId())) {
                m.setRequiredForCompletion(true);
            }
        });
        return course;
    }

    @Override
    public List<LearningTag> getAllLearningTags() {
        log.info("Getting all organisational units from csrs");
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(config.getLearningTagUrl());
        return httpClient.getPaginatedRequest(LearningTagsPagedResponse.class, uriBuilder,
                config.getLearningTagMaxPageSize()).stream().toList();
    }

    @Override
    public LearningTag createLearningTag(LearningTagDTO dto) {
        return httpClient.executeRequest(RequestEntity.post(config.getLearningTagUrl()).body(dto), LearningTag.class);
    }

    @Override
    public LearningTag updateLearningTag(Long id, LearningTagDTO dto) {
        String url = config.getLearningTagUrl(id);
        return httpClient.executeRequest(RequestEntity.put(url).body(dto), LearningTag.class);
    }

    @Override
    public BulkUpdateResponse updateLearningTagState(Collection<Long> ids, LearningTagStateUpdate stateUpdate) {
        BulkLearningTagStateDto dto = new BulkLearningTagStateDto(ids, stateUpdate.getName());
        return httpClient.executeRequest(RequestEntity.put(config.getLearningTagStateUrl()).body(dto), BulkUpdateResponse.class);
    }

    @Override
    public CourseLearningTagDto addLearningTagToCourse(String courseUid, LearningTagDTO learningTagDTO) {
        String url = String.format("%s/%s/learning-tags", config.getCourseUrl(), courseUid);
        RequestEntity<LearningTagDTO> request = RequestEntity.post(url).body(learningTagDTO);
        return httpClient.executeRequest(request, CourseLearningTagDto.class);
    }

    @Override
    public void removeLearningTagFromCourse(String courseUid, String learningTagCode) {
        String url = String.format("%s/%s/learning-tags/%s", config.getCourseUrl(), courseUid, learningTagCode);
        RequestEntity<Void> request = RequestEntity.delete(url).build();
        httpClient.executeRequest(request, Void.class);
    }

    @Override
    public CourseSearchResults getCoursesForLearningTag(Long tagId, int page, int size) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(config.getLearningTagUrl(tagId) + "/courses");
        uriBuilder.queryParam("page", page);
        uriBuilder.queryParam("size", size);
        RequestEntity<Void> request = RequestEntity.get(uriBuilder.toUriString()).build();
        CourseSearchResults resp = httpClient.executeRequest(request, CourseSearchResults.class);
        if (resp != null && resp.getResults() != null) {
            resp.getResults().forEach(this::buildCourseData);
        }
        return resp;
    }
}
