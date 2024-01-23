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
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

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

    @Autowired
    private CSLStubService cslStubService;

    @Test
    public void TestRemoveCourseFromLearningPlan() {
        String courseId = testDataService.getCourseId();
        String userId = testDataService.getUserId();
        CourseRecord inProgressCourseRecord = testDataService.generateCourseRecord(false);
        inProgressCourseRecord.setState(State.IN_PROGRESS);
        CourseRecords courseRecords = new CourseRecords(List.of(inProgressCourseRecord));
        cslStubService.getLearnerRecord().getCourseRecord(courseId, userId, courseRecords);

        CourseRecord archivedCourseRecord = testDataService.generateCourseRecord(false);
        archivedCourseRecord.setState(State.ARCHIVED);
        List<PatchOp> expectedPatches = List.of(PatchOp.replacePatch("state", "ARCHIVED"));
        cslStubService.getLearnerRecord().patchCourseRecord(expectedPatches, archivedCourseRecord);

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
                .jsonPath("$.message").isEqualTo("Successfully applied action 'Remove from Learning plan' to course record");

    }

    @Test
    public void TestRemoveCourseFromLearningPlanCourseRecordNotFound() {
        String courseId = testDataService.getCourseId();
        String userId = testDataService.getUserId();
        CourseRecords courseRecords = new CourseRecords();
        cslStubService.getLearnerRecord().getCourseRecord(courseId, userId, courseRecords);

        webTestClient
                .post()
                .uri(String.format("/courses/%s/remove_from_learning_plan", courseId))
                .header("Authorization", "Bearer fakeToken")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Record is in the incorrect state")
                .jsonPath("$.status").isEqualTo("400")
                .jsonPath("$.instance").isEqualTo("/api/courses/courseId/remove_from_learning_plan");
    }
}
