package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;
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
import uk.gov.cabinetoffice.csl.domain.rustici.UserDetailsDto;
import uk.gov.cabinetoffice.csl.util.CSLServiceWireMockServer;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "360000")
@ActiveProfiles({"wiremock", "no-redis", "jms"})
@Import(TestConfig.class)
public class CompleteEventTest extends CSLServiceWireMockServer {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private TestDataService testDataService;
    private String courseId;
    private String userId;
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
        moduleId = testDataService.getModuleId();
        eventId = testDataService.getEventId();
        courseRecord = testDataService.generateCourseRecord(true);
        courseRecords = new CourseRecords(List.of(courseRecord));
        moduleRecord = courseRecord.getModuleRecord(moduleId).get();
        course = testDataService.generateCourse(true, true);
    }

    @Test
    @SneakyThrows
    public void testCompleteBookingAndUpdateCourseRecord() {
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
        moduleRecord.setState(State.APPROVED);
        courseRecord.setState(State.APPROVED);
        cslStubService.getLearningCatalogue().getCourse(courseId, course);
        cslStubService.getLearnerRecord().getCourseRecord(courseId, userId, courseRecords);
        UserDetailsDto dto = testDataService.generateUserDetailsDto();
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
                            "state": "COMPLETED"
                        }
                    ]
                }
                """;
        cslStubService.getLearnerRecord().updateCourseRecord(expectedCourseRecordPUT, courseRecord);
        String url = String.format("/courses/%s/modules/%s/events/%s/complete_booking", courseId, moduleId, eventId);
        webTestClient
                .post()
                .uri(url)
                .header("Authorization", "Bearer fakeToken")
                .body(Mono.just(dto), UserDetailsDto.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Successfully applied action 'Complete a booking' to course record");
    }

    @Test
    public void testCompleteBookingNotApproved() {
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
        moduleRecord.setState(State.REGISTERED);
        cslStubService.getLearningCatalogue().getCourse(courseId, course);
        cslStubService.getLearnerRecord().getCourseRecord(courseId, userId, courseRecords);
        UserDetailsDto dto = testDataService.generateUserDetailsDto();
        String url = String.format("/courses/%s/modules/%s/events/%s/complete_booking", courseId, moduleId, eventId);
        webTestClient
                .post()
                .uri(url)
                .body(Mono.just(dto), UserDetailsDto.class)
                .header("Authorization", "Bearer fakeToken")
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Can't complete a booking that hasn't been approved")
                .jsonPath("$.title").isEqualTo("Record is in the incorrect state");
        ;
    }

}
