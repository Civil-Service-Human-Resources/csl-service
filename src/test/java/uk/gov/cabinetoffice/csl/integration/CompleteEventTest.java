package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.rustici.UserDetailsDto;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

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
                            "state": "COMPLETED",
                            "completionDate": "2023-01-01T10:00:00"
                        }
                    ]
                }
                """;
        cslStubService.getLearnerRecord().updateCourseRecord(expectedCourseRecordPUT, courseRecord);
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
        moduleRecord.setState(State.REGISTERED);
        cslStubService.getLearningCatalogue().getCourse(courseId, course);
        cslStubService.getLearnerRecord().getCourseRecord(courseId, userId, courseRecords);
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
