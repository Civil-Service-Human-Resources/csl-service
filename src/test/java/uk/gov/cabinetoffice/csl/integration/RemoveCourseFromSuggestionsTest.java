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
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordInput;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Preference;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.CSLServiceWireMockServer;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.util.List;

import static uk.gov.cabinetoffice.csl.util.stub.LearnerRecordStubService.createCourseRecord;
import static uk.gov.cabinetoffice.csl.util.stub.LearnerRecordStubService.getCourseRecord;
import static uk.gov.cabinetoffice.csl.util.stub.LearningCatalogueStubService.getCourse;

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

    @Test
    public void TestRemoveCourseFromSuggestions() {
        CourseRecord courseRecord = testDataService.generateCourseRecord(false);
        String courseId = testDataService.getCourseId();
        String userId = testDataService.getUserId();
        Course course = testDataService.generateCourse(true);
        getCourseRecord(courseId, userId, null);
        CourseRecordInput expectedInput = new CourseRecordInput(courseId, userId,
                course.getTitle(), null, null, Preference.DISLIKED.name(),
                List.of());
        getCourse(courseId, course);
        createCourseRecord(expectedInput, courseRecord);
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
                .jsonPath("$.message").isEqualTo("Successfully applied action 'REMOVE_FROM_SUGGESTIONS' to course record");

    }

}
