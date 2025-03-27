package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.math.BigDecimal;
import java.util.List;

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
        userEmail = testDataService.getUserEmail();
        moduleId = testDataService.getModuleId();
        eventId = testDataService.getEventId();
        courseRecord = testDataService.generateCourseRecord(true);
        courseRecords = new CourseRecords(List.of(courseRecord));
        moduleRecord = courseRecord.getModuleRecord(moduleId).get();
        course = testDataService.generateCourse(true, true);
        bookingId = 1;
    }

    @Test
    public void testApproveBookingAndUpdateCourseRecord() throws Exception {
        course.getModule(moduleId).setCost(BigDecimal.valueOf(0L));
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
        courseRecord.setState(State.REGISTERED);
        String expectedCancellationJsonInput = """
                {"status":"Confirmed"}
                """;
        String event = String.format("http://localhost:9000/learning_catalogue/courses/%s/modules/%s/events/%s", courseId, moduleId, eventId);
        String bookingDtoJsonResponse = String.format("""
                {"event": "%s", "status":"Confirmed", "learner": "%s", "learnerEmail": "%s", "learnerName":"%s"}
                """, event, userId, userEmail, testDataService.getLearnerFirstName());
        String expectedCourseRecordPUT = """
                [{
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "state" : "APPROVED",
                    "modules": [
                        {
                            "id" : 1,
                            "moduleId" : "moduleId",
                            "moduleTitle" : "Test Module",
                            "eventId" : "eventId",
                            "eventDate" : "2023-01-01",
                            "state": "APPROVED"
                        }
                    ]
                }]
                """;
        cslStubService.getLearnerRecord().updateBookingWithId(eventId, bookingId, expectedCancellationJsonInput, bookingDtoJsonResponse);
        cslStubService.stubUpdateCourseRecord(courseId, course, userId, courseRecords, expectedCourseRecordPUT, courseRecord);
        String url = String.format("/admin/courses/%s/modules/%s/events/%s/bookings/%s/approve_booking", courseId, moduleId, eventId, bookingId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message")
                        .value("Successfully applied action 'Approve a booking' to course record"));
    }

}
