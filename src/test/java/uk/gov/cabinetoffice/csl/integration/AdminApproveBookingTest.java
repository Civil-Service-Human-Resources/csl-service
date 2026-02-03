package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class AdminApproveBookingTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;
    private String courseId;
    private String userId;
    private String userEmail;
    private String moduleId;
    private String eventId;
    private Integer bookingId;
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
        bookingId = 1;
    }

    @Test
    public void testApproveBookingAndUpdateCourseRecord() throws Exception {
        course.getModule(moduleId).setCost(BigDecimal.valueOf(0L));
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
        String expectedCancellationJsonInput = """
                {"status":"Confirmed"}
                """;
        String event = String.format("http://localhost:9000/learning_catalogue/courses/%s/modules/%s/events/%s", courseId, moduleId, eventId);
        String bookingDtoJsonResponse = String.format("""
                {"event": "%s", "status":"Confirmed", "learner": "%s", "learnerEmail": "%s", "learnerName":"%s",
                "bookingReference": "ABCDE"}
                """, event, userId, userEmail, testDataService.getLearnerFirstName());
        String getModuleRecordsResponse = """
                {"moduleRecords": [{
                    "id" : 1,
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "eventId" : "eventId",
                    "eventDate" : "2023-01-01",
                    "state": "REGISTERED"
                }]}
                """;
        String expectedModuleRecordPUT = """
                [{
                    "id" : 1,
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "eventId" : "eventId",
                    "eventDate" : "2023-01-01",
                    "state": "APPROVED"
                }]
                """;
        String expectedModuleRecordPUTResponse = """
                {"moduleRecords":[{
                    "id" : 1,
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "eventId" : "eventId",
                    "eventDate" : "2023-01-01",
                    "state": "APPROVED"
                }]}
                """;
        cslStubService.getNotificationServiceStubService().sendEmail("BOOKING_CONFIRMED",
                """
                        {
                            "recipient": "userEmail@email.com",
                            "personalisation" : {
                              "learnerName" : "userEmail@email.com",
                              "courseTitle" : "Test Course",
                              "courseLocation" : "London",
                              "accessibility" : "",
                              "bookingReference" : "ABCDE",
                              "courseDate" : "01 Jan 2023"
                            }
                        }
                        """);
        cslStubService.getNotificationServiceStubService().sendEmail("BOOKING_CONFIRMED_LINE_MANAGER",
                """
                        {
                            "recipient" : "lineManager@email.com",
                            "personalisation" : {
                              "recipient" : "lineManager@email.com",
                              "courseDate" : "01 Jan 2023",
                              "learnerEmail" : "userEmail@email.com",
                              "learnerName" : "Learner",
                              "courseTitle" : "Test Course",
                              "cost" : "0",
                              "courseLocation" : "London"
                            }
                        }
                        """);
        cslStubService.getCsrsStubService().getCivilServant("userId", testDataService.generateCivilServant());
        cslStubService.getLearnerRecord().updateBookingWithId(eventId, bookingId, expectedCancellationJsonInput, bookingDtoJsonResponse);
        cslStubService.stubUpdateModuleRecord(course, moduleId, userId, getModuleRecordsResponse, expectedModuleRecordPUT, expectedModuleRecordPUTResponse);
        String url = String.format("/admin/courses/%s/modules/%s/events/%s/bookings/%s/approve_booking", courseId, moduleId, eventId, bookingId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.bookingId").value("1"))
                .andExpect(jsonPath("$.learner").value("userId"));
    }

}
