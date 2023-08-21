package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import uk.gov.cabinetoffice.csl.configuration.TestConfig;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLinkRequest;
import uk.gov.cabinetoffice.csl.domain.rustici.ModuleLaunchLinkInput;
import uk.gov.cabinetoffice.csl.util.CSLServiceWireMockServer;
import uk.gov.cabinetoffice.csl.util.StringUtilService;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.util.List;

import static org.mockito.Mockito.when;
import static uk.gov.cabinetoffice.csl.util.stub.LearnerRecordStubService.*;
import static uk.gov.cabinetoffice.csl.util.stub.RusticiStubService.postLaunchLink;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles({"wiremock", "no-redis"})
@Import(TestConfig.class)
public class ModuleLaunchTest extends CSLServiceWireMockServer {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataService testDataService;

    @MockBean
    private StringUtilService stringUtilService;

    /**
     * Data
     */
    private String courseId;
    private String userId;
    private String moduleId;
    private CourseRecord courseRecord;
    private CourseRecords courseRecords;
    private ModuleRecord moduleRecord;
    private ModuleLaunchLinkInput input;
    private LaunchLink launchLink;

    @PostConstruct
    public void populateTestData() {
        courseId = testDataService.getCourseId();
        userId = testDataService.getUserId();
        moduleId = testDataService.getModuleId();
        courseRecord = testDataService.generateCourseRecord(true);
        courseRecords = new CourseRecords(List.of(courseRecord));
        moduleRecord = courseRecord.getModuleRecord(moduleId);
        input = new ModuleLaunchLinkInput();
        input.setLearnerFirstName(testDataService.getLearnerFirstName());
        launchLink = new LaunchLink("http://launch.link");
    }

    @Test
    public void testGetLaunchLink() {
        moduleRecord.setUid(null);
        when(stringUtilService.generateRandomUuid()).thenReturn("uid");
        getCourseRecord(courseId, userId, courseRecords);
        patchModuleRecord(1, List.of(
                PatchOp.replacePatch("updatedAt", "2023-01-01T10:00"),
                PatchOp.replacePatch("uid", "uid")
        ), moduleRecord);
        patchCourseRecord(List.of(
                PatchOp.replacePatch("state", "IN_PROGRESS")
        ), courseRecord);
        LaunchLinkRequest req = testDataService.generateLaunchLinkRequest();
        postLaunchLink("uid", req, launchLink);

        String url = String.format("/courses/%s/modules/%s/launch", courseId, moduleId);
        webTestClient
                .post()
                .uri(url)
                .header("Authorization", "Bearer fakeToken")
                .body(Mono.just(input), ModuleLaunchLinkInput.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.launchLink")
                .isEqualTo("http://launch.link");
    }
//
//    @Test
//    public void testGetLaunchLinkCreateRecords() {
//        moduleRecord.setUid(null);
//        when(stringUtilService.generateRandomUuid()).thenReturn("uid");
//        learningCatalogueStubService.getCourse(courseId, course);
//        learnerRecordStubService.getCourseRecord(courseId, userId, null);
//        LaunchLinkRequest req = testDataService.generateLaunchLinkRequest();
//        rusticiStubService.postLaunchLink("uid", req, launchLink);
//
//        String url = String.format("/courses/%s/modules/%s/launch", courseId, moduleId);
//        webTestClient
//                .post()
//                .uri(url)
//                .header("Authorization", "Bearer fakeToken")
//                .body(Mono.just(input), ModuleLaunchLinkInput.class)
//                .exchange()
//                .expectStatus()
//                .is2xxSuccessful()
//                .expectBody()
//                .jsonPath("$.launchLink")
//                .isEqualTo("http://launch.link");
//    }

}
