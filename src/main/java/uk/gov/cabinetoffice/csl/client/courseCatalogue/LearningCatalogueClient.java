package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseFactory;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.RequiredLearningMap;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LearningCatalogueClient implements ILearningCatalogueClient {

    @Value("${learningCatalogue.courseUrl}")
    private String courses;
    @Value("${learningCatalogue.courseV2Url}")
    private String v2Courses;
    private final IHttpClient httpClient;
    private final CourseFactory courseFactory;

    public LearningCatalogueClient(@Qualifier("learningCatalogueHttpClient") IHttpClient httpClient,
                                   CourseFactory courseFactory) {
        this.httpClient = httpClient;
        this.courseFactory = courseFactory;
    }

    @Override
    public List<Course> getCourses(Collection<String> courseIds) {
        log.info("Getting courses with ids '{}' from learning catalogue API", courseIds);
        String url = String.format("%s?courseId=%s", courses, String.join(",", courseIds));
        RequestEntity<Void> request = RequestEntity.get(url).build();
        List<Course> course = httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        });
        return course.stream().map(this::buildCourseData).collect(Collectors.toList());
    }

    @Override
    public RequiredLearningMap getRequiredLearningIdMap() {
        String url = String.format("%s/required-learning-map", v2Courses);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeRequest(request, RequiredLearningMap.class);
    }

    @Override
    public Event updateEvent(String courseId, String moduleId, Event event) {
        String url = String.format("%s/%s/modules/%s/events/%s", courses, courseId, moduleId, event.getId());
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
}
