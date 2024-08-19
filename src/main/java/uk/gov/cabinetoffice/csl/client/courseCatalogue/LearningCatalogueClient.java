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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LearningCatalogueClient implements ILearningCatalogueClient {

    @Value("${learningCatalogue.courseUrl}")
    private String courses;

    private final IHttpClient httpClient;
    private final CourseFactory courseFactory;

    public LearningCatalogueClient(@Qualifier("learningCatalogueHttpClient") IHttpClient httpClient,
                                   CourseFactory courseFactory) {
        this.httpClient = httpClient;
        this.courseFactory = courseFactory;
    }

    @Override
    public List<Course> getCourses(GetCourseParams params) {
        log.info("Getting courses with params '{}' from learning catalogue API", params);
        String url = String.format("%s%s", courses, params.getUrlParams());
        RequestEntity<Void> request = RequestEntity.get(url).build();
        List<Course> course = httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        });
        return course.stream().map(this::buildCourseData).collect(Collectors.toList());
    }

    @Override
    public List<Course> getCoursesWithIds(List<String> courseIds) {
        GetCourseParams params = GetCourseParams.builder().courseIds(courseIds).build();
        return this.getCourses(params);
    }

    @Override
    public Course getCourse(String courseId) {
        return this.getCoursesWithIds(List.of(courseId)).stream().findFirst().orElse(null);
    }

    private Course buildCourseData(Course course) {
        Map<String, Integer> departmentCodeToRequiredAudienceMap = courseFactory.buildRequiredLearningDepartmentMap(course.getAudiences());
        course.setDepartmentCodeToRequiredAudienceMap(departmentCodeToRequiredAudienceMap);

        List<String> moduleIdsRequiredForCompletion = courseFactory.getRequiredModulesForCompletion(course.getModules());
        course.setRequiredModuleIdsForCompletion(moduleIdsRequiredForCompletion);
        return course;
    }
}
