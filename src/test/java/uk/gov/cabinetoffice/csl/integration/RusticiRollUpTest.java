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
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;
import uk.gov.cabinetoffice.csl.util.CSLServiceWireMockServer;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles({"wiremock", "no-redis", "jms"})
@Import(TestConfig.class)
public class RusticiRollUpTest extends CSLServiceWireMockServer {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CSLStubService cslStubService;

    CourseRecord courseRecord;
    CourseRecords courseRecords;
    RusticiRollupData rollupData;

    @PostConstruct
    public void setupData() {
        courseRecord = testDataService.generateCourseRecord(true);
        courseRecords = new CourseRecords(List.of(courseRecord));
        rollupData = testDataService.generateRusticiRollupData();
    }


    @Test
    public void testRollUpCompletedModuleRecord() {
        Course course = testDataService.generateCourse(true, false);
        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.stubGetUserDetails(testDataService.getUserId(), civilServant);
        String expectedCourseRecordPUT = """
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
                            "state": "COMPLETED",
                            "completionDate": "2023-01-01T10:00:00"
                        }
                    ]
                }
                """;
        cslStubService.stubUpdateCourseRecord(testDataService.getCourseId(), course, testDataService.getUserId(),
                courseRecords, expectedCourseRecordPUT, courseRecord);

        webTestClient
                .post()
                .uri("/rustici/rollup")
                .body(Mono.just(rollupData), RusticiRollupData.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    @Test
    public void testRollUpFailedModuleRecord() {
        Course course = testDataService.generateCourse(true, false);
        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.stubGetUserDetails(testDataService.getUserId(), civilServant);
        RusticiRollupData failedRollUpData = testDataService.generateRusticiRollupData();
        failedRollUpData.setRegistrationSuccess("FAILED");
        failedRollUpData.setCompletedDate(null);
        String expectedCourseRecordPUT = """
                {
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "state" : "NULL",
                    "modules": [
                        {
                            "id" : 1,
                            "moduleId" : "moduleId",
                            "moduleTitle" : "Test Module",
                            "state": "NULL",
                            "result": "FAILED"
                        }
                    ]
                }
                """;
        cslStubService.stubUpdateCourseRecord(testDataService.getCourseId(), course, testDataService.getUserId(),
                courseRecords, expectedCourseRecordPUT, courseRecord);

        webTestClient
                .post()
                .uri("/rustici/rollup")
                .body(Mono.just(failedRollUpData), RusticiRollupData.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

}
