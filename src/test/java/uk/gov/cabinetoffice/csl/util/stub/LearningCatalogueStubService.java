package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static uk.gov.cabinetoffice.csl.util.CslTestUtil.toJson;

public class LearningCatalogueStubService {

    public static void getCourse(String courseId, Course response) {
        stubFor(
                WireMock.get(urlPathEqualTo(String.format("/learning_catalogue/courses/%s", courseId)))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(toJson(response)))
        );
    }

}
