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
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.util.CSLServiceWireMockServer;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.util.List;

import static uk.gov.cabinetoffice.csl.util.stub.LearnerRecordStubService.getCourseRecord;
import static uk.gov.cabinetoffice.csl.util.stub.LearnerRecordStubService.patchCourseRecord;

@Slf4j
@ActiveProfiles({"wiremock", "no-redis"})
@Import(TestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class RemoveCourseFromLearningPlanTest extends CSLServiceWireMockServer {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataService testDataService;

    @Test
    public void TestRemoveCourseFromLearningPlan() {
        String courseId = testDataService.getCourseId();
        String userId = testDataService.getUserId();
        CourseRecord inProgressCourseRecord = testDataService.generateCourseRecord(false);
        inProgressCourseRecord.setState(State.IN_PROGRESS);
        CourseRecords courseRecords = new CourseRecords(List.of(inProgressCourseRecord));
        getCourseRecord(courseId, userId, courseRecords);

        CourseRecord archivedCourseRecord = testDataService.generateCourseRecord(false);
        archivedCourseRecord.setState(State.ARCHIVED);
        List<PatchOp> expectedPatches = List.of(PatchOp.replacePatch("state", "ARCHIVED"));
        patchCourseRecord(expectedPatches, archivedCourseRecord);

        webTestClient
                .post()
                .uri(String.format("/courses/%s/remove_from_learning_plan", courseId))
                .header("Authorization", "Bearer fakeToken")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.courseId").isEqualTo(courseId)
                .jsonPath("$.courseTitle").isEqualTo(testDataService.getCourseTitle())
                .jsonPath("$.message").isEqualTo("Successfully applied action 'REMOVE_FROM_LEARNING_PLAN' to course record");

    }

    @Test
    public void TestRemoveCourseFromLearningPlanCourseRecordNotFound() {
        String courseId = testDataService.getCourseId();
        String userId = testDataService.getUserId();
        CourseRecords courseRecords = new CourseRecords();
        getCourseRecord(courseId, userId, courseRecords);

        webTestClient
                .post()
                .uri(String.format("/courses/%s/remove_from_learning_plan", courseId))
                .header("Authorization", "Bearer fakeToken")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Record Not Found")
                .jsonPath("$.status").isEqualTo("404")
                .jsonPath("$.instance").isEqualTo("/api/courses/courseId/remove_from_learning_plan");
    }
}
