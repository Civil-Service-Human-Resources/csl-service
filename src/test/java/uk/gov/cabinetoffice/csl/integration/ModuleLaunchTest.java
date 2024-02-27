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
import reactor.core.publisher.Mono;
import uk.gov.cabinetoffice.csl.configuration.TestConfig;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLinkRequest;
import uk.gov.cabinetoffice.csl.domain.rustici.UserDetailsDto;
import uk.gov.cabinetoffice.csl.util.CSLServiceWireMockServer;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

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

    @Autowired
    private CSLStubService cslStubService;

    /**
     * Data
     */
    private String courseId;
    private String userId;
    private String moduleId;
    private CourseRecord courseRecord;
    private CourseRecords courseRecords;
    private Course course;
    private ModuleRecord moduleRecord;
    private UserDetailsDto input;
    private LaunchLink launchLink;

    @PostConstruct
    public void populateTestData() {
        courseId = testDataService.getCourseId();
        userId = testDataService.getUserId();
        moduleId = testDataService.getModuleId();
        courseRecord = testDataService.generateCourseRecord(true);
        courseRecords = new CourseRecords(List.of(courseRecord));
        moduleRecord = courseRecord.getModuleRecord(moduleId).get();
        course = testDataService.generateCourse(true, false);
        input = testDataService.generateUserDetailsDto();
        launchLink = new LaunchLink("http://launch.link");
    }

    @Test
    public void testGetELearningLaunchLinkUidExists() {
        String expectedCourseRecordPUT = """
                {
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "state" : "IN_PROGRESS",
                    "modules": [
                        {
                            "id" : 1,
                            "moduleId" : "moduleId",
                            "moduleTitle" : "Test Module",
                            "state": "IN_PROGRESS"
                        }
                    ]
                }
                """;
        moduleRecord.setState(State.IN_PROGRESS);
        cslStubService.stubUpdateCourseRecord(courseId, course, userId, courseRecords, expectedCourseRecordPUT, courseRecord);
        LaunchLinkRequest req = testDataService.generateLaunchLinkRequest();
        cslStubService.getRustici().postLaunchLink("uid", req, launchLink, false);

        String url = String.format("/courses/%s/modules/%s/launch", courseId, moduleId);
        webTestClient
                .post()
                .uri(url)
                .header("Authorization", "Bearer fakeToken")
                .body(Mono.just(input), UserDetailsDto.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.launchLink")
                .isEqualTo("http://launch.link");
    }

    @Test
    public void testGetFileLaunchLink() {
        moduleRecord.setUid(null);
        course.getModule(moduleId).setModuleType(ModuleType.file);
        course.getModule(moduleId).setUrl("http://launch.link");
        cslStubService.getLearningCatalogue().getCourse(courseId, course);
        cslStubService.getLearnerRecord().getCourseRecord(courseId, userId, courseRecords);
        String expectedCourseRecordPOST = """
                {
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "state" : "COMPLETED",
                    "modules": [
                        {
                            "id" : 1,
                            "moduleId" : "moduleId",
                            "moduleTitle" : "Test Module",
                            "state": "COMPLETED"
                        }
                    ]
                }
                """;
        cslStubService.getLearnerRecord().updateCourseRecord(expectedCourseRecordPOST, courseRecord);
        String url = String.format("/courses/%s/modules/%s/launch", courseId, moduleId);
        webTestClient
                .post()
                .uri(url)
                .header("Authorization", "Bearer fakeToken")
                .body(Mono.just(input), UserDetailsDto.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.launchLink")
                .isEqualTo("http://launch.link");
    }

    @Test
    public void testLaunchNewCourse() {
        course.getModule(moduleId).setModuleType(ModuleType.file);
        course.getModule(moduleId).setUrl("http://launch.link");
        String expectedCourseRecordPOST = """
                {
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "state" : "COMPLETED",
                    "modules": [
                        {
                            "id" : null,
                            "moduleId" : "moduleId",
                            "moduleTitle" : "Test Module",
                            "state": "COMPLETED"
                        }
                    ]
                }
                """;
        cslStubService.stubCreateCourseRecord(courseId, course, userId, expectedCourseRecordPOST, courseRecord);

        String url = String.format("/courses/%s/modules/%s/launch", courseId, moduleId);
        webTestClient
                .post()
                .uri(url)
                .header("Authorization", "Bearer fakeToken")
                .body(Mono.just(input), UserDetailsDto.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.launchLink")
                .isEqualTo("http://launch.link");
    }

}
