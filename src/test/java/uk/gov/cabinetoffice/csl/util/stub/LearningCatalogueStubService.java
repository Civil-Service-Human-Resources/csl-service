package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Component
public class LearningCatalogueStubService {

    private final CslTestUtil utils;

    public LearningCatalogueStubService(CslTestUtil utils) {
        this.utils = utils;
    }

    public void getCourse(String courseId, Course response) {
        getCourses(List.of(courseId), List.of(response));
    }

    public void getCourses(List<String> courseIds, List<Course> response) {
        getCourses(courseIds, utils.toJson(response));
    }

    public void getMandatoryLearningMap(String response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/learning_catalogue/v2/courses/required-learning-map"))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public void getCourses(List<String> courseIds, String response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/learning_catalogue/courses"))
                        .withQueryParam("courseId", equalTo(String.join(",", courseIds)))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

}
