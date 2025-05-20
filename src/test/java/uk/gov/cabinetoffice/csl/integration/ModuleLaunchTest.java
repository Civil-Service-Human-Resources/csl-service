package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLinkRequest;
import uk.gov.cabinetoffice.csl.domain.rustici.UserDetailsDto;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class ModuleLaunchTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CSLStubService cslStubService;

    /**
     * Data
     */
    private String courseId;
    private String userId;
    private String moduleId;
    private Course course;
    private UserDetailsDto input;
    private LaunchLink launchLink;

    @PostConstruct
    public void populateTestData() {
        courseId = testDataService.getCourseId();
        userId = testDataService.getUserId();
        moduleId = testDataService.getModuleId();
        course = testDataService.generateCourse(true, false);
        input = testDataService.generateUserDetailsDto();
        launchLink = new LaunchLink("http://launch.link");
    }

    @Test
    public void testGetELearningLaunchLinkUidExists() throws Exception {
        String getModuleRecordsResponse = """
                {"moduleRecords": [{
                    "id" : 1,
                    "uid": "uid",
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "IN_PROGRESS"
                }]}
                """;
        String expectedModuleRecordPUT = """
                [{
                    "id" : 1,
                    "uid": "uid",
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "IN_PROGRESS"
                }]
                """;
        String expectedModuleRecordPUTResponse = """
                {"moduleRecords":[{
                    "id" : 1,
                    "uid": "uid",
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "IN_PROGRESS"
                }]}
                """;
        cslStubService.stubUpdateModuleRecord(course, moduleId, userId, getModuleRecordsResponse, expectedModuleRecordPUT, expectedModuleRecordPUTResponse);
        LaunchLinkRequest req = testDataService.generateLaunchLinkRequest();
        cslStubService.getRustici().postLaunchLink("uid", req, launchLink, false);

        String url = String.format("/courses/%s/modules/%s/launch", courseId, moduleId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utils.toJson(input)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.launchLink").value("http://launch.link"));
    }

    @Test
    public void testGetFileLaunchLink() throws Exception {
        course.getModule(moduleId).setModuleType(ModuleType.file);
        course.getModule(moduleId).setUrl("http://launch.link");
        cslStubService.getLearningCatalogue().getCourse(courseId, course);
        String getModuleRecordsResponse = """
                {"moduleRecords": [{
                    "id" : 1,
                    "uid": "uid",
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "IN_PROGRESS"
                }]}
                """;
        cslStubService.getLearnerRecord().getModuleRecord(moduleId, userId, getModuleRecordsResponse);
        String expectedModuleRecordPUT = """
                [{
                    "id" : 1,
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "COMPLETED"
                }]
                """;
        String expectedModuleRecordPUTResponse = """
                [{
                    "id" : 1,
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "COMPLETED"
                }]
                """;
        cslStubService.getLearnerRecord().updateModuleRecords(expectedModuleRecordPUT, expectedModuleRecordPUTResponse);
        String url = String.format("/courses/%s/modules/%s/launch", courseId, moduleId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utils.toJson(input)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.launchLink").value("http://launch.link"));
    }

    @Test
    public void testCompleteRequiredCourse() throws Exception {
        Course requiredCourse = testDataService.generateCourse(true, false);
        requiredCourse.setAudiences(List.of(
                testDataService.generateRequiredAudience(input.getDepartmentHierarchy().get(0).getCode())
        ));
        requiredCourse.getModule(moduleId).setModuleType(ModuleType.file);
        requiredCourse.getModule(moduleId).setUrl("http://launch.link");
        cslStubService.getLearningCatalogue().getCourse(courseId, requiredCourse);
        cslStubService.getLearnerRecord().getLearnerRecords("userId", "courseId", 0, """
                {
                    "content": [],
                    "totalPages": 0
                }
                """);
        cslStubService.getLearnerRecord().getModuleRecord(moduleId, userId, """
                {"moduleRecords": []}
                """);
        String expectedLearnerRecordsPOST = """
                [
                    {
                        "recordType" : "COURSE",
                        "learnerId": "userId",
                        "resourceId": "courseId",
                        "createdTimestamp" : "2023-01-01T10:00:00",
                        "events" : [{
                            "learnerId": "userId",
                            "resourceId": "courseId",
                            "eventType": "COMPLETE_COURSE",
                            "eventTimestamp" : "2023-01-01T10:00:00",
                            "eventSource": "csl_source_id"
                        }]
                    }
                ]
                """;
        String expectedLearnerRecordsPOSTResponse = """
                {
                    "successfulResources": [{
                        "recordType" : {"type": "COURSE"},
                        "learnerId": "userId",
                        "resourceId": "courseId",
                        "createdTimestamp" : "2023-01-01T10:00:00",
                        "events" : [{
                            "learnerId": "userId",
                            "resourceId": "courseId",
                            "eventType": "COMPLETE_COURSE",
                            "eventTimestamp" : "2023-01-01T10:00:00",
                            "eventSource": {"source": "csl_source_id"}
                        }]
                    }],
                    "failedResources": []
                }
                """;
        String expectedModuleRecordPOST = """
                [{
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "COMPLETED",
                    "completionDate" : "2023-01-01T10:00:00"
                }]
                """;
        String expectedModuleRecordPOSTResponse = """
                {"moduleRecords":[{
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "COMPLETED",
                    "completionDate" : "2023-01-01T10:00:00"
                }]}
                """;
        cslStubService.getLearnerRecord().createModuleRecords(expectedModuleRecordPOST, expectedModuleRecordPOSTResponse);
        cslStubService.getLearnerRecord().createLearnerRecords(expectedLearnerRecordsPOST, expectedLearnerRecordsPOSTResponse);
        String expectedMessageDto = """
                {
                    "recipient": "lineManager@email.com",
                    "personalisation": {
                        "manager": "Manager",
                        "learner": "Learner",
                        "learnerEmailAddress": "userEmail@email.com",
                        "courseTitle": "Test Course"
                    }
                }
                """;
        cslStubService.getNotificationServiceStubService().sendEmail("NOTIFY_LINE_MANAGER_COMPLETED_LEARNING", expectedMessageDto);
        String url = String.format("/courses/%s/modules/%s/launch", courseId, moduleId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utils.toJson(input)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.launchLink").value("http://launch.link"));
        cslStubService.getNotificationServiceStubService().validateSentEmails("NOTIFY_LINE_MANAGER_COMPLETED_LEARNING", 1);
    }

    @Test
    public void testLaunchNewCourse() throws Exception {
        course.getModule(moduleId).setModuleType(ModuleType.file);
        course.getModule(moduleId).setUrl("http://launch.link");
        cslStubService.getLearnerRecord().getLearnerRecords("userId", "courseId", 0, """
                {
                    "content": [],
                    "totalPages": 0
                }
                """);
        String expectedLearnerRecordsPOST = """
                [
                    {
                        "recordType" : "COURSE",
                        "learnerId": "userId",
                        "resourceId": "courseId",
                        "createdTimestamp" : "2023-01-01T10:00:00",
                        "events" : [{
                            "learnerId": "userId",
                            "resourceId": "courseId",
                            "eventType": "COMPLETE_COURSE",
                            "eventTimestamp" : "2023-01-01T10:00:00",
                            "eventSource": "csl_source_id"
                        }]
                    }
                ]
                """;
        String expectedLearnerRecordsPOSTResponse = """
                {
                    "successfulResources": [{
                        "recordType" : {"type": "COURSE"},
                        "learnerId": "userId",
                        "resourceId": "courseId",
                        "createdTimestamp" : "2023-01-01T10:00:00",
                        "events" : [{
                            "learnerId": "userId",
                            "resourceId": "courseId",
                            "eventType": "COMPLETE_COURSE",
                            "eventTimestamp" : "2023-01-01T10:00:00",
                            "eventSource": {"source": "csl_source_id"}
                        }]
                    }],
                    "failedResources": []
                }
                """;
        String expectedModuleRecordPOST = """
                [{
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "COMPLETED",
                    "completionDate" : "2023-01-01T10:00:00"
                }]
                """;
        String expectedModuleRecordPOSTResponse = """
                {"moduleRecords":[{
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "COMPLETED",
                    "completionDate" : "2023-01-01T10:00:00"
                }]}
                """;
        cslStubService.stubCreateModuleRecords(courseId, moduleId, course, userId, expectedModuleRecordPOST, expectedModuleRecordPOSTResponse);
        cslStubService.getLearnerRecord().createLearnerRecords(expectedLearnerRecordsPOST, expectedLearnerRecordsPOSTResponse);
        String url = String.format("/courses/%s/modules/%s/launch", courseId, moduleId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utils.toJson(input)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.launchLink").value("http://launch.link"));
    }

}
