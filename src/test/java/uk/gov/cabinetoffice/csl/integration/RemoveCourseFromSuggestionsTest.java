package uk.gov.cabinetoffice.csl.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import uk.gov.cabinetoffice.csl.configuration.TestConfig;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.CSLServiceWireMockServer;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

@Slf4j
@ActiveProfiles({"wiremock", "no-redis"})
@Import(TestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class RemoveCourseFromSuggestionsTest extends CSLServiceWireMockServer {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CSLStubService cslStubService;

    @Test
    public void TestRemoveCourseFromSuggestions() {
        CourseRecord courseRecord = testDataService.generateCourseRecord(false);
        String courseId = testDataService.getCourseId();
        String userId = testDataService.getUserId();
        Course course = testDataService.generateCourse(true, false);
        String expectedCourseRecordPOST = """
                {
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "preference": "DISLIKED",
                    "state": null
                }
                """;
        cslStubService.stubCreateCourseRecord(courseId, course, userId, expectedCourseRecordPOST, courseRecord);
        webTestClient
                .post()
                .uri(String.format("/courses/%s/remove_from_suggestions", courseId))
                .header("Authorization", "Bearer fakeToken")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.courseId").isEqualTo(courseId)
                .jsonPath("$.courseTitle").isEqualTo(testDataService.getCourseTitle())
                .jsonPath("$.message").isEqualTo("Successfully applied action 'Remove from suggestions' to course record");

    }

}
