package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.rustici.UserDetailsDto;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class CompleteEventTest extends IntegrationTestBase {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private TestDataService testDataService;
    private String courseId;
    private String userId;
    private String moduleId;
    private String eventId;
    private Course course;

    @Autowired
    private CSLStubService cslStubService;

    @PostConstruct
    public void populateTestData() {
        courseId = testDataService.getCourseId();
        userId = testDataService.getUserId();
        moduleId = testDataService.getModuleId();
        eventId = testDataService.getEventId();
        course = testDataService.generateCourse(true, true);
    }

    @Test
    @SneakyThrows
    public void testCompleteBookingAndUpdateCourseRecord() {
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
        cslStubService.getLearningCatalogue().getCourse(courseId, course);
        String getModuleRecordsResponse = """
                {"moduleRecords": [{
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
        cslStubService.getLearnerRecord().getModuleRecord(moduleId, userId, getModuleRecordsResponse);
        UserDetailsDto dto = testDataService.generateUserDetailsDto();
        String expectedModuleRecordPUT = """
                [{
                    "id" : 1,
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "COMPLETED",
                    "completionDate": "2023-01-01T10:00:00"
                }]
                """;
        String expectedModuleRecordPUTResponse = """
                {"moduleRecords":[{
                    "id" : 1,
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "COMPLETED",
                    "completionDate": "2023-01-01T10:00:00"
                }]}
                """;
        cslStubService.getLearnerRecord().updateModuleRecords(expectedModuleRecordPUT, expectedModuleRecordPUTResponse);
        String url = String.format("/courses/%s/modules/%s/events/%s/complete_booking", courseId, moduleId, eventId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utils.toJson(dto)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message").value("Successfully applied action 'Complete a booking' to course record"));
    }

    @Test
    public void testCompleteBookingNotApproved() throws Exception {
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
        cslStubService.getLearningCatalogue().getCourse(course);
        cslStubService.getLearnerRecord().getModuleRecord(moduleId, userId, """
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
                """);
        UserDetailsDto dto = testDataService.generateUserDetailsDto();
        String url = String.format("/courses/%s/modules/%s/events/%s/complete_booking", courseId, moduleId, eventId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utils.toJson(dto)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.detail").value("Can't complete a booking that hasn't been approved"))
                .andExpect(jsonPath("$.title").value("Record is in the incorrect state"));
    }

}
