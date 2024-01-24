package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import uk.gov.cabinetoffice.csl.configuration.TestConfig;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.util.CSLServiceWireMockServer;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles({"wiremock", "no-redis"})
@Import(TestConfig.class)
public class CompleteEventTest extends CSLServiceWireMockServer {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataService testDataService;
    private String courseId;
    private String userId;
    private String userEmail;
    private String moduleId;
    private String eventId;
    private CourseRecord courseRecord;
    private CourseRecords courseRecords;
    private Course course;
    private ModuleRecord moduleRecord;

    @Autowired
    private CSLStubService cslStubService;

    @PostConstruct
    public void populateTestData() {
        courseId = testDataService.getCourseId();
        userId = testDataService.getUserId();
        userEmail = testDataService.getUseremail();
        moduleId = testDataService.getModuleId();
        eventId = testDataService.getEventId();
        courseRecord = testDataService.generateCourseRecord(true);
        courseRecords = new CourseRecords(List.of(courseRecord));
        moduleRecord = courseRecord.getModuleRecord(moduleId);
        course = testDataService.generateCourse(true, true);
    }

    @Test
    public void testCompleteBookingAndUpdateCourseRecord() {
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
        ModuleRecord mr = courseRecord.getModuleRecord(moduleId);
        mr.setState(State.APPROVED);
        cslStubService.getLearningCatalogue().getCourse(courseId, course);
        cslStubService.getLearnerRecord().getCourseRecord(courseId, userId, courseRecords);
        mr.setState(State.COMPLETED);
        List<PatchOp> expectedModuleRecordPatches = List.of(
                PatchOp.replacePatch("state", "COMPLETED"),
                PatchOp.replacePatch("completionDate", "2023-01-01T10:00")
        );
        cslStubService.getLearnerRecord().patchModuleRecord(mr.getId(), expectedModuleRecordPatches, mr);
        List<PatchOp> expectedCourseRecordPatches = List.of(PatchOp.replacePatch("state", "COMPLETED"));
        cslStubService.getLearnerRecord().patchCourseRecord(expectedCourseRecordPatches, courseRecord);
        String url = String.format("/courses/%s/modules/%s/events/%s/complete_booking", courseId, moduleId, eventId);
        webTestClient
                .post()
                .uri(url)
                .header("Authorization", "Bearer fakeToken")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Module booking was successfully completed");
    }

}