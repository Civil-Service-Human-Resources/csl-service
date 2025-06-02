package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class SkipEventTest extends IntegrationTestBase {

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
    public void testSkipBookingAndUpdateModuleRecord() throws Exception {
        course.getModule(moduleId).setModuleType(ModuleType.facetoface);
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
        String expectedModuleRecordPUT = """
                [{
                    "id" : 1,
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "eventId" : "eventId",
                    "eventDate" : "2023-01-01",
                    "state": "SKIPPED"
                }]
                """;
        String expectedModuleRecordPUTResponse = """
                {"moduleRecords": [{
                    "id" : 1,
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "eventId" : "eventId",
                    "eventDate" : "2023-01-01",
                    "state": "SKIPPED"
                }]}
                """;
        cslStubService.stubUpdateModuleRecord(course, moduleId, userId, getModuleRecordsResponse, expectedModuleRecordPUT, expectedModuleRecordPUTResponse);
        String url = String.format("/courses/%s/modules/%s/events/%s/skip_booking", courseId, moduleId, eventId);
        mockMvc.perform(post(url))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message").value("Successfully applied action 'Skip a booking' to course record"));
    }

}
