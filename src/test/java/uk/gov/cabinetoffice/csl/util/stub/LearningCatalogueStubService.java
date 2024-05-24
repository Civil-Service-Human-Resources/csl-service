package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.courseCatalogue.model.GetCoursesResponse;
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
        GetCoursesResponse coursesResponse = new GetCoursesResponse(response);
        stubFor(
                WireMock.get(urlPathEqualTo("/learning_catalogue/courses"))
                        .withQueryParam("courseIds", equalTo(String.join(",", courseIds)))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(utils.toJson(coursesResponse)))
        );
    }

}
