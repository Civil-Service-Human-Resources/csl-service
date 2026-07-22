package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.SearchForCoursesParams;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Component
public class LearningCatalogueStubService {

    private final CslTestUtil utils;

    public LearningCatalogueStubService(CslTestUtil utils) {
        this.utils = utils;
    }

    public StubMapping getCourse(Course course) {
        return getCourses(List.of(course.getId()), List.of(course));
    }

    public StubMapping getCourse(String courseId, Course response) {
        return getCourses(List.of(courseId), List.of(response));
    }

    public StubMapping getCourses(List<String> courseIds, List<Course> response) {
        return getCourses(courseIds, utils.toJson(response));
    }

    public StubMapping getMandatoryLearningMap(String response) {
        return stubFor(
                WireMock.get(urlPathEqualTo("/learning_catalogue/v2/courses/required-learning-map"))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping getCourses(List<String> courseIds, String response) {
        return stubFor(
                WireMock.get(urlPathEqualTo("/learning_catalogue/courses"))
                        .withQueryParam("courseId", havingExactly(courseIds.toArray(String[]::new)))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping updateEvent(String courseId, String moduleId, String eventId, Event expectedInput) {
        return stubFor(
                WireMock.put(urlPathEqualTo(String.format("/learning_catalogue/courses/%s/modules/%s/events/%s", courseId, moduleId, eventId)))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .withRequestBody(equalToJson(utils.toJson(expectedInput), true, true))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(utils.toJson(expectedInput)))
        );
    }

    public StubMapping postSearchCourses(SearchForCoursesParams params, String courses, Integer page, Integer size, String sortBy, String sortDirection) {
        return stubFor(
                WireMock.post(urlPathEqualTo("/learning_catalogue/v2/courses/search"))
                        .withQueryParam("size", equalTo(size.toString()))
                        .withQueryParam("page", equalTo(page.toString()))
                        .withQueryParam("sort.field", equalTo(sortBy))
                        .withQueryParam("sort.direction", equalTo(sortDirection))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .withRequestBody(equalToJson(utils.toJson(params), true, false))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(courses))
        );
    }

    public StubMapping getAudienceMetadataMap(String response) {
        return stubFor(
                WireMock.get(urlPathEqualTo("/learning_catalogue/v2/courses/audience-attribute-map"))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping getLearningTags(String response) {
        return stubFor(
                WireMock.get(urlPathEqualTo("/learning_catalogue/learning-tags"))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping createLearningTag(String input, String response) {
        return stubFor(
                WireMock.post(urlPathEqualTo("/learning_catalogue/learning-tags"))
                        .withRequestBody(equalToJson(input, true, false))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping updateLearningTag(int learningTagId, String expectedInput, String response) {
        return stubFor(
                WireMock.put(urlPathEqualTo(String.format("/learning_catalogue/learning-tags/%s", learningTagId)))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping addLearningTagToCourse(String courseUid, String input, String response) {
        return stubFor(
                WireMock.post(urlPathEqualTo(String.format("/learning_catalogue/courses/%s/learning-tags", courseUid)))
                        .withRequestBody(equalToJson(input, true, false))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping removeLearningTagFromCourse(String courseUid, String learningTagCode) {
        return stubFor(
                WireMock.delete(urlPathEqualTo(String.format("/learning_catalogue/courses/%s/learning-tags/%s", courseUid, learningTagCode)))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withStatus(204))
        );
    }
}
