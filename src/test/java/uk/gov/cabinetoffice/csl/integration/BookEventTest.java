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
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingStatus;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.util.CSLServiceWireMockServer;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles({"wiremock", "no-redis"})
@Import(TestConfig.class)
public class BookEventTest extends CSLServiceWireMockServer {

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
        moduleRecord = courseRecord.getModuleRecord(moduleId).get();
        course = testDataService.generateCourse(true, true);
    }

    @Test
    public void testBookFreeEventAndCreateCourseRecord() {
        course.getModule(moduleId).setCost(BigDecimal.valueOf(0L));
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
        BookingDto dto = BookingDto.builder()
                .accessibilityOptions("access1,access2")
                .event(URI.create(String.format("http://localhost:9000/learning_catalogue/courses/%s/modules/%s/events/%s", courseId, moduleId, eventId)))
                .status(BookingStatus.CONFIRMED)
                .learner(userId)
                .learnerEmail(userEmail)
                .learnerName("testName").build();
        String expectedCourseRecordPOST = """
                {
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "state" : "APPROVED",
                    "modules": [
                        {
                            "id" : null,
                            "moduleId" : "moduleId",
                            "moduleTitle" : "Test Module",
                            "state": "APPROVED",
                            "eventId" : "eventId",
                            "eventDate" : "2023-01-01"
                        }
                    ]
                }
                """;
        String expectedBookingJsonInput = String.format("""
                        {"event": "%s", "learner":"%s", "learnerEmail":"%s", "learnerName":"%s", "bookingTime":"%s",
                        "accessibilityOptions": "%s", "status": "%s"}
                        """, dto.getEvent(), userId, userEmail, testDataService.getLearnerFirstName(), "2023-01-01T10:00:00Z", "access1,access2",
                "Confirmed");
        cslStubService.getLearnerRecord().bookEvent(eventId, expectedBookingJsonInput, dto);
        cslStubService.stubCreateCourseRecord(courseId, course, userId, expectedCourseRecordPOST, courseRecord);
        BookEventDto inputDto = new BookEventDto(List.of("access1", "access2"), "", testDataService.generateUserDetailsDto());
        String url = String.format("/courses/%s/modules/%s/events/%s/create_booking", courseId, moduleId, eventId);
        webTestClient
                .post()
                .uri(url)
                .header("Authorization", "Bearer fakeToken")
                .body(Mono.just(inputDto), BookEventDto.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Successfully applied action 'Approve a booking' to course record");
    }

    @Test
    public void testBookFreeEventAndUpdateCourseRecord() {
        course.getModule(moduleId).setCost(BigDecimal.valueOf(0L));
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
        courseRecord.setState(State.IN_PROGRESS);
        BookingDto dto = BookingDto.builder()
                .event(URI.create(String.format("http://localhost:9000/learning_catalogue/courses/%s/modules/%s/events/%s", courseId, moduleId, eventId)))
                .status(BookingStatus.CONFIRMED)
                .learner(userId)
                .learnerEmail(userEmail)
                .learnerName("testName").build();
        String expectedBookingJsonInput = String.format("""
                {"event": "%s", "learner":"%s", "learnerEmail":"%s", "learnerName":"%s", "bookingTime":"%s",
                "status": "%s", "accessibilityOptions" : ""}
                """, dto.getEvent(), userId, userEmail, testDataService.getLearnerFirstName(), "2023-01-01T10:00:00Z", "Confirmed");
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
                            "state": "APPROVED",
                            "eventId" : "eventId",
                            "eventDate" : "2023-01-01"
                        }
                    ]
                }
                """;
        cslStubService.getLearnerRecord().bookEvent(eventId, expectedBookingJsonInput, dto);
        cslStubService.stubUpdateCourseRecord(courseId, course, userId, courseRecords, expectedCourseRecordPUT, courseRecord);
        BookEventDto inputDto = new BookEventDto(List.of(), "", testDataService.generateUserDetailsDto());
        String url = String.format("/courses/%s/modules/%s/events/%s/create_booking", courseId, moduleId, eventId);
        webTestClient
                .post()
                .uri(url)
                .header("Authorization", "Bearer fakeToken")
                .body(Mono.just(inputDto), BookEventDto.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Successfully applied action 'Approve a booking' to course record");
    }

    @Test
    public void testBookPaidEventAndCreateCourseRecord() {
        course.getModule(moduleId).setCost(BigDecimal.valueOf(5L));
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
        BookingDto dto = BookingDto.builder()
                .accessibilityOptions("access1")
                .event(URI.create(String.format("http://localhost:9000/learning_catalogue/courses/%s/modules/%s/events/%s", courseId, moduleId, eventId)))
                .status(BookingStatus.REQUESTED)
                .learner(userId)
                .poNumber("poNumber123")
                .learnerEmail(userEmail)
                .learnerName("testName").build();
        String expectedBookingJsonInput = String.format("""
                        {"event": "%s", "learner":"%s", "learnerEmail":"%s", "learnerName":"%s", "bookingTime":"%s",
                        "accessibilityOptions": "%s", "status": "%s", "poNumber":"%s"}
                        """, dto.getEvent(), userId, userEmail, testDataService.getLearnerFirstName(), "2023-01-01T10:00:00Z", "access1",
                "Requested", "poNumber123");
        String expectedCourseRecordPOST = """
                {
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "state" : "REGISTERED",
                    "modules": [
                        {
                            "id" : null,
                            "moduleId" : "moduleId",
                            "moduleTitle" : "Test Module",
                            "state": "REGISTERED",
                            "eventId" : "eventId",
                            "eventDate" : "2023-01-01"
                        }
                    ]
                }
                """;
        cslStubService.getLearnerRecord().bookEvent(eventId, expectedBookingJsonInput, dto);
        cslStubService.stubCreateCourseRecord(courseId, course, userId, expectedCourseRecordPOST, courseRecord);
        BookEventDto inputDto = new BookEventDto(List.of("access1"), "poNumber123", testDataService.generateUserDetailsDto());
        String url = String.format("/courses/%s/modules/%s/events/%s/create_booking", courseId, moduleId, eventId);
        webTestClient
                .post()
                .uri(url)
                .header("Authorization", "Bearer fakeToken")
                .body(Mono.just(inputDto), BookEventDto.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Successfully applied action 'Register for an event' to course record");
    }
}
