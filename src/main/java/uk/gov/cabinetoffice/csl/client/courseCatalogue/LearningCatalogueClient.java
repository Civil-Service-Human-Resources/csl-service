package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseFactory;

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
    @Cacheable(value = "catalogue-course", key = "#courseId", unless = "#result == null")
    public Course getCourse(String courseId) {
        try {
            log.info("Getting course with ID '{}' from learning catalogue API", courseId);
            String url = String.format("%s/%s", courses, courseId);
            RequestEntity<Void> request = RequestEntity.get(url).build();
            Course course = httpClient.executeRequest(request, Course.class);
            return courseFactory.buildCourseData(course);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                return null;
            }
            throw e;
        }
    }
}
