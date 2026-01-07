package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class AdminManagementTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;
    private String courseId;
    private String moduleId;
    private String eventId;
    private Course course;
    @Autowired
    private CSLStubService cslStubService;

    @PostConstruct
    public void populateTestData() {
        courseId = testDataService.getCourseId();
        moduleId = testDataService.getModuleId();
        eventId = testDataService.getEventId();

        course = testDataService.generateCourse(true, true);
    }

    @Test
    public void testRenderEventOverview() throws Exception {
        cslStubService.getLearningCatalogue().getCourse(course);
        String eventDtoResponse = """
                {
                    "uid": "eventId",
                    "status": "Active",
                    "activeBookings": [
                        {
                            "id": 1,
                            "learner": "learner1",
                            "status": "Requested",
                            "bookingReference": "ABC12"
                        },
                        {
                            "id": 2,
                            "learner": "learner2",
                            "status": "Confirmed",
                            "bookingReference": "DEF12"
                        },
                        {
                            "id": 3,
                            "learner": "learner3",
                            "status": "Confirmed",
                            "bookingReference": "GHI12"
                        }
                    ],
                    "invites": [
                        {
                            "learnerEmail": "learner1@email.com"
                        }
                    ]
                }
                """;
        cslStubService.getLearnerRecord().findEvent(eventId, true, true, eventDtoResponse);
        cslStubService.getIdentityAPIServiceStubService().getIdentityMap(List.of("learner1", "learner2", "learner3"),
                """
                        {
                         "learner1": {
                             "username": "learner1@email.com"
                         },
                         "learner2": {
                             "username": "learner2@email.com"
                         }
                        }
                        """);
        String url = String.format("/admin/management/courses/%s/modules/%s/events/%s/overview", courseId, moduleId, eventId);
        mockMvc.perform(get(url))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("eventId"))
                .andExpect(jsonPath("$.venue.location").value("London"))
                .andExpect(jsonPath("$.venue.address").value("1 London Road"))
                .andExpect(jsonPath("$.venue.capacity").value("10"))
                .andExpect(jsonPath("$.venue.minCapacity").value("5"))
                .andExpect(jsonPath("$.venue.availability").value("7"))
                .andExpect(jsonPath("$.dates[0]").value("01 Jan 2023"))
                .andExpect(jsonPath("$.status").value("Active"))
                .andExpect(jsonPath("$.moduleId").value("moduleId"))
                .andExpect(jsonPath("$.moduleTitle").value("Test Module"))
                .andExpect(jsonPath("$.courseId").value("courseId"))
                .andExpect(jsonPath("$.courseTitle").value("Test Course"))
                .andExpect(jsonPath("$.courseStatus").value("Published"))

                .andExpect(jsonPath("$.invitedEmails.length()").value("1"))
                .andExpect(jsonPath("$.invitedEmails[0]").value("learner1@email.com"))

                .andExpect(jsonPath("$.bookings.length()").value("2"))
                .andExpect(jsonPath("$.bookings[0].id").value("1"))
                .andExpect(jsonPath("$.bookings[0].reference").value("ABC12"))
                .andExpect(jsonPath("$.bookings[0].learnerEmail").value("learner1@email.com"))
                .andExpect(jsonPath("$.bookings[0].status").value("Requested"))
                .andExpect(jsonPath("$.bookings[1].id").value("2"))
                .andExpect(jsonPath("$.bookings[1].reference").value("DEF12"))
                .andExpect(jsonPath("$.bookings[1].learnerEmail").value("learner2@email.com"))
                .andExpect(jsonPath("$.bookings[1].status").value("Confirmed"));
    }
}
