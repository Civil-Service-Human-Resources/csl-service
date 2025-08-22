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
public class AdminCancelBookingTest extends IntegrationTestBase {

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
    public void testCancelBookingAndUpdateModuleRecord() throws Exception {
        course.getModule(moduleId).setCost(BigDecimal.valueOf(0L));
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
        String expectedCancellationJsonInput = """
                {"cancellationReason": "PAYMENT", "status":"Cancelled"}
                """;
        String event = String.format("http://localhost:9000/learning_catalogue/courses/%s/modules/%s/events/%s", courseId, moduleId, eventId);
        String bookingDtoJsonResponse = String.format("""
                {"event": "%s", "status":"Cancelled", "learner": "%s",
                "learnerEmail": "%s", "learnerName":"testName", "cancellationReason": "the booking has not been paid"}
                """, event, userId, userEmail);
        String getModuleRecordsResponse = """
                {"moduleRecords": [{
                    "id" : 1,
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
                    "courseId": "courseid",
                    "moduleId" : "moduleId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "UNREGISTERED",
                    "bookingStatus": "Cancelled"
                }]
                """;
        String expectedModuleRecordPUTResponse = """
                {"moduleRecords":[{
                    "id" : 1,
                    "userId": "userId",
                    "courseId": "courseid",
                    "moduleId" : "moduleId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "UNREGISTERED",
                    "bookingStatus": "Cancelled"
                }]}
                """;
        cslStubService.getLearnerRecord().updateBookingWithId(eventId, bookingId, expectedCancellationJsonInput, bookingDtoJsonResponse);
        cslStubService.stubUpdateModuleRecord(course, moduleId, userId, getModuleRecordsResponse, expectedModuleRecordPUT, expectedModuleRecordPUTResponse);
        String inputJson = """
                {"reason": "PAYMENT"}
                """;
        String url = String.format("/admin/courses/%s/modules/%s/events/%s/bookings/%s/cancel_booking", courseId, moduleId, eventId, bookingId);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.bookingId").value("1"))
                .andExpect(jsonPath("$.learner").value("userId"));

    }

}
