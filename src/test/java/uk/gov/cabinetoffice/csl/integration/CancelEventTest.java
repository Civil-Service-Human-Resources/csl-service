package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.EventCancellationReason;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.EventStatus;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class CancelEventTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;
    private String courseId;
    private String userId;
    private String userId2;
    private String moduleId;
    private String eventId;
    private CourseRecord courseRecord;
    private CourseRecord courseRecord2;
    private CourseRecords courseRecords;
    private Course course;
    private Event event;

    @Autowired
    private CSLStubService cslStubService;

    @PostConstruct
    public void populateTestData() {
        courseId = testDataService.getCourseId();
        userId = testDataService.getUserId();
        userId2 = userId + "2";
        moduleId = testDataService.getModuleId();
        eventId = testDataService.getEventId();
        courseRecord = testDataService.generateCourseRecord(true);
        courseRecord.setState(State.APPROVED);
        courseRecord2 = testDataService.generateCourseRecord(true);
        courseRecord2.setUserId(userId2);
        courseRecord2.setState(State.APPROVED);
        courseRecord2.getModuleRecord(moduleId).get().setId(2L);
        courseRecords = new CourseRecords(List.of(courseRecord, courseRecord2));
        course = testDataService.generateCourse(true, true);
        event = course.getModule(moduleId).getEvent(eventId);
    }

    @Test
    public void testCancelEventAndUpdateCourseRecord() throws Exception {
        event.setCancellationReason(EventCancellationReason.VENUE);
        event.setStatus(EventStatus.CANCELLED);
        cslStubService.getLearningCatalogue().updateEvent(courseId, moduleId, eventId, event);
        cslStubService.getLearnerRecord().getBookings(eventId, """
                [{
                    "learner": "userId",
                    "learnerEmail": "userId@email.com",
                    "status": "REQUESTED",
                    "bookingReference": "ABC"
                },
                {
                    "learner": "userId2",
                    "learnerEmail": "userId2@email.com",
                    "status": "CONFIRMED",
                    "bookingReference": "DEF"
                },
                {
                    "learner": "userId3",
                    "learnerEmail": "userId3@email.com",
                    "status": "CANCELLED",
                    "bookingReference": "GHI"
                }]
                """);
        cslStubService.getLearnerRecord().cancelEvent(eventId, """
                {"cancellationReason": "VENUE", "status":"CANCELLED"}
                """);
        cslStubService.stubUpdateCourseRecords(
                List.of(new CourseRecordId(userId, courseId), new CourseRecordId(userId2, courseId)),
                List.of(course), courseRecords, """
                        [{
                            "courseId" : "courseId",
                            "userId" : "userId",
                            "courseTitle" : "Test Course",
                            "state" : "UNREGISTERED",
                            "modules": [
                                {
                                    "id" : 1,
                                    "moduleId" : "moduleId",
                                    "moduleTitle" : "Test Module",
                                    "state": "UNREGISTERED",
                                    "bookingStatus": "Cancelled"
                                }
                            ]
                        },
                        {
                            "courseId" : "courseId",
                            "userId" : "userId2",
                            "courseTitle" : "Test Course",
                            "state" : "UNREGISTERED",
                            "modules": [
                                {
                                    "id" : 2,
                                    "moduleId" : "moduleId",
                                    "moduleTitle" : "Test Module",
                                    "state": "UNREGISTERED",
                                    "bookingStatus": "Cancelled"
                                }
                            ]
                        }]
                        """, courseRecords);
        cslStubService.getNotificationServiceStubService().sendEmail("NOTIFY_LEARNER_CANCELLED_EVENT",
                """
                        {
                            "recipient": "userId@email.com",
                            "personalisation": {
                                "learnerName": "userId@email.com",
                                "cancellationReason": "short notice unavailability of the venue",
                                "courseTitle": "Test Course",
                                "courseDate": "01 Jan 2023",
                                "courseLocation": "London",
                                "bookingReference": "ABC"
                            }
                        }
                        """);
        cslStubService.getNotificationServiceStubService().sendEmail("NOTIFY_LEARNER_CANCELLED_EVENT",
                """
                        {
                            "recipient": "userId2@email.com",
                            "personalisation": {
                                "learnerName": "userId2@email.com",
                                "cancellationReason": "short notice unavailability of the venue",
                                "courseTitle": "Test Course",
                                "courseDate": "01 Jan 2023",
                                "courseLocation": "London",
                                "bookingReference": "DEF"
                            }
                        }
                        """);
        String url = String.format("/admin/courses/%s/modules/%s/events/%s/cancel", courseId, moduleId, eventId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reason": "VENUE"}
                                """))
                .andExpect(status().is2xxSuccessful());
        cslStubService.getNotificationServiceStubService().validateSentEmails("NOTIFY_LEARNER_CANCELLED_EVENT", 2);
    }

}
