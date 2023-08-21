package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

@Service
@Slf4j
public class LearningCatalogueClient implements ILearningCatalogueClient {

    @Value("${learningCatalogue.courseUrl}")
    private String courses;

    private final IHttpClient httpClient;

    public LearningCatalogueClient(@Qualifier("learningCatalogueHttpClient") IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Course getCourse(String courseId) {
        log.info("Getting course with ID '{}'", courseId);
        String url = String.format("%s/%s", courses, courseId);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeRequest(request, Course.class);
    }
}
