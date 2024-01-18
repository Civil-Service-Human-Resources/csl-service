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
import uk.gov.cabinetoffice.csl.controller.model.CancelBookingDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.util.CSLServiceWireMockServer;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles({"wiremock", "no-redis"})
@Import(TestConfig.class)
public class CancelEventTest extends CSLServiceWireMockServer {

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
    public void testCancelBookingAndUpdateCourseRecord() {
        course.getModule(moduleId).setCost(BigDecimal.valueOf(0L));
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
        courseRecord.setState(State.REGISTERED);
        BookingDto dto = BookingDto.builder()
                .event(URI.create(String.format("http://localhost:9000/learning_catalogue/courses/%s/modules/%s/events/%s", courseId, moduleId, eventId)))
                .status(BookingStatus.CANCELLED)
                .learner(userId)
                .learnerEmail(userEmail)
                .learnerName("testName").build();
        List<PatchOp> expectedCourseRecordPatches = List.of(PatchOp.replacePatch("state", "UNREGISTERED"));
        List<PatchOp> expectedModuleRecordPatches = List.of(
                PatchOp.replacePatch("state", "UNREGISTERED"),
                PatchOp.replacePatch("bookingStatus", "CANCELLED"),
                PatchOp.removePatch("result"),
                PatchOp.removePatch("score"),
                PatchOp.removePatch("completionDate")
        );
        String expectedCancellationJsonInput = """
                {"cancellationReason": "ILLNESS", "status":"Cancelled"}
                """;
        cslStubService.getLearnerRecord().cancelBooking(eventId, userId, expectedCancellationJsonInput, dto);
        cslStubService.stubUpdateCourseRecord(courseId, course, userId, courseRecords,
                1, expectedModuleRecordPatches, moduleRecord, expectedCourseRecordPatches, courseRecord);
        CancelBookingDto inputDto = new CancelBookingDto(BookingCancellationReason.ILLNESS);
        String url = String.format("/courses/%s/modules/%s/events/%s/cancel_booking", courseId, moduleId, eventId);
        webTestClient
                .post()
                .uri(url)
                .header("Authorization", "Bearer fakeToken")
                .body(Mono.just(inputDto), CancelBookingDto.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Module booking was successfully cancelled");
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
        CourseRecordInput expectedCourseRecordInput = CourseRecordInput.from(
                userId, course, CourseRecordStatus.builder().state("REGISTERED").build(),
                course.getModule(moduleId), ModuleRecordStatus.builder()
                        .state("REGISTERED").uid(null)
                        .eventId(eventId).eventDate(LocalDate.of(2023, 1, 1)).build());
        String expectedBookingJsonInput = String.format("""
                        {"event": "%s", "learner":"%s", "learnerEmail":"%s", "learnerName":"%s", "bookingTime":"%s",
                        "accessibilityOptions": "%s", "status": "%s", "poNumber":"%s"}
                        """, dto.getEvent(), userId, userEmail, "testName", "2023-01-01T10:00:00Z", "access1",
                "Requested", "poNumber123");
        cslStubService.getLearnerRecord().bookEvent(eventId, expectedBookingJsonInput, dto);
        cslStubService.stubCreateCourseRecord(courseId, course, userId, expectedCourseRecordInput, courseRecord);
        BookEventDto inputDto = new BookEventDto(List.of("access1"), "poNumber123", "userEmail@email.com", "testName");
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
                .isEqualTo("Module was successfully booked");
    }
}
