package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingStatus;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class BookEventTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;
    private String courseId;
    private String userId;
    private String userEmail;
    private String moduleId;
    private String eventId;
    private Course course;

    @Autowired
    private CSLStubService cslStubService;

    @PostConstruct
    public void populateTestData() {
        courseId = testDataService.getCourseId();
        userId = testDataService.getUserId();
        userEmail = testDataService.getUserEmail();
        moduleId = testDataService.getModuleId();
        eventId = testDataService.getEventId();
        course = testDataService.generateCourse(true, true);
    }

    @Test
    public void testBookFreeEventAndCreateModuleRecord() throws Exception {
        course.getModule(moduleId).setCost(BigDecimal.valueOf(0L));
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
        BookingDto dto = BookingDto.builder()
                .accessibilityOptions("access1,access2")
                .event(URI.create(String.format("http://localhost:9000/learning_catalogue/courses/%s/modules/%s/events/%s", courseId, moduleId, eventId)))
                .status(BookingStatus.CONFIRMED)
                .learner(userId)
                .bookingReference("ABC12")
                .learnerEmail(userEmail)
                .learnerName("testName").build();
        String expectedModuleRecordPOST = """
                [{
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "APPROVED",
                    "eventId" : "eventId",
                    "eventDate" : "2023-01-01"
                }]
                """;
        String expectedModuleRecordPOSTResponse = """
                {"moduleRecords":[{
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "APPROVED",
                    "eventId" : "eventId",
                    "eventDate" : "2023-01-01"
                }]}
                """;
        String expectedBookingJsonInput = String.format("""
                        {"event": "%s", "learner":"%s", "learnerEmail":"%s", "learnerName":"%s", "bookingTime":"%s",
                        "accessibilityOptions": "%s", "status": "%s"}
                        """, dto.getEvent(), userId, userEmail, testDataService.getLearnerFirstName(), "2023-01-01T10:00:00Z", "access1,access2",
                "Confirmed");
        cslStubService.getCsrsStubService().getCivilServant("userId", testDataService.generateCivilServant());
        cslStubService.getLearnerRecord().bookEvent(eventId, expectedBookingJsonInput, dto);
        cslStubService.stubCreateModuleRecords(courseId, moduleId, course, userId, expectedModuleRecordPOST, expectedModuleRecordPOSTResponse);
        cslStubService.getNotificationServiceStubService().sendEmail("BOOKING_CONFIRMED",
                """
                        {
                            "recipient" : "userEmail@email.com",
                            "personalisation" : {
                              "accessibility" : "access1,access2",
                              "courseLocation" : "London",
                              "courseTitle" : "Test Course",
                              "learnerName" : "userEmail@email.com",
                              "courseDate" : "01 Jan 2023",
                              "bookingReference" : "ABC12"
                            }
                        }
                        """);
        cslStubService.getNotificationServiceStubService().sendEmail("BOOKING_CONFIRMED_LINE_MANAGER",
                """
                        {
                            "recipient" : "lineManager@email.com",
                            "personalisation" : {
                              "courseLocation" : "London",
                              "bookingReference" : "ABC12",
                              "recipient" : "lineManager@email.com",
                              "courseDate" : "01 Jan 2023",
                              "learnerEmail" : "userEmail@email.com",
                              "learnerName" : "Learner",
                              "courseTitle" : "Test Course",
                              "cost" : "0"
                            }
                        }
                        """);
        BookEventDto inputDto = new BookEventDto(List.of("access1", "access2"), "");
        String url = String.format("/courses/%s/modules/%s/events/%s/create_booking", courseId, moduleId, eventId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utils.toJson(inputDto)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message").value("Successfully applied action 'Approve a booking' to course record"));
    }

    @Test
    public void testBookPaidEventAndCreateModuleRecord() throws Exception {
        course.getModule(moduleId).setCost(BigDecimal.valueOf(5L));
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
        BookingDto dto = BookingDto.builder()
                .accessibilityOptions("access1")
                .event(URI.create(String.format("http://localhost:9000/learning_catalogue/courses/%s/modules/%s/events/%s", courseId, moduleId, eventId)))
                .status(BookingStatus.REQUESTED)
                .learner(userId)
                .bookingReference("ABC12")
                .poNumber("poNumber123")
                .learnerEmail(userEmail)
                .learnerName("testName").build();
        String expectedBookingJsonInput = String.format("""
                        {"event": "%s", "learner":"%s", "learnerEmail":"%s", "learnerName":"%s", "bookingTime":"%s",
                        "accessibilityOptions": "%s", "status": "%s", "poNumber":"%s"}
                        """, dto.getEvent(), userId, userEmail, testDataService.getLearnerFirstName(), "2023-01-01T10:00:00Z", "access1",
                "Requested", "poNumber123");
        String expectedModuleRecordPOST = """
                [{
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "REGISTERED",
                    "eventId" : "eventId",
                    "eventDate" : "2023-01-01"
                }]
                """;
        String expectedModuleRecordPOSTResponse = """
                {"moduleRecords":[{
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "REGISTERED",
                    "eventId" : "eventId",
                    "eventDate" : "2023-01-01"
                }]}
                """;
        cslStubService.getLearnerRecord().bookEvent(eventId, expectedBookingJsonInput, dto);
        cslStubService.stubCreateModuleRecords(courseId, moduleId, course, userId, expectedModuleRecordPOST, expectedModuleRecordPOSTResponse);
        cslStubService.getCsrsStubService().getCivilServant("userId", testDataService.generateCivilServant());
        cslStubService.getNotificationServiceStubService().sendEmail("BOOKING_REQUESTED",
                """
                        {
                            "recipient" : "userEmail@email.com",
                            "personalisation" : {
                              "accessibility" : "access1",
                              "courseLocation" : "London",
                              "courseTitle" : "Test Course",
                              "learnerName" : "userEmail@email.com",
                              "courseDate" : "01 Jan 2023",
                              "bookingReference" : "ABC12"
                            }
                        }
                        """);
        cslStubService.getNotificationServiceStubService().sendEmail("BOOKING_REQUEST_LINE_MANAGER",
                """
                        {
                            "recipient" : "lineManager@email.com",
                            "personalisation" : {
                              "courseLocation" : "London",
                              "bookingReference" : "ABC12",
                              "recipient" : "lineManager@email.com",
                              "courseDate" : "01 Jan 2023",
                              "learnerEmail" : "userEmail@email.com",
                              "learnerName" : "Learner",
                              "courseTitle" : "Test Course",
                              "cost" : "5"
                            }
                        }
                        """);
        BookEventDto inputDto = new BookEventDto(List.of("access1"), "poNumber123");
        String url = String.format("/courses/%s/modules/%s/events/%s/create_booking", courseId, moduleId, eventId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utils.toJson(inputDto)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message").value("Successfully applied action 'Register for an event' to course record"));
    }
}
