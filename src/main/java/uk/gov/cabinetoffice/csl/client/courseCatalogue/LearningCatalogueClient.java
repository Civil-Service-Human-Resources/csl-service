package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Audience;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseFactory;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;

import java.util.Collection;
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

    public LearningCatalogueClient(@Qualifier("learningCatalogueHttpClient") IHttpClient httpClient, CourseFactory courseFactory) {
        this.httpClient = httpClient;
        this.courseFactory = courseFactory;
    }

    @Override
    public List<Course> getCourses(List<String> courseIds) {
        log.info("Getting courses with IDs '{}' from learning catalogue API", courseIds);
        String url = String.format("%s?courseIds=%s", courses, String.join(",", courseIds));
        RequestEntity<Void> request = RequestEntity.get(url).build();
        List<Course> course = httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        });
        return course.stream().map(this::buildCourseData).collect(Collectors.toList());
    }

    @Override
    public Course getCourse(String courseId) {
        return this.getCourses(List.of(courseId)).stream().findFirst().orElse(null);
    }

    private Course buildCourseData(Course course) {
        Collection<Audience> audiences = course.getAudiences();
        Map<String, LearningPeriod> departmentDeadlineMap = courseFactory.buildDepartmentDeadlineMap(audiences);
        course.setDepartmentDeadlineMap(departmentDeadlineMap);
        return course;
    }
}
