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
    private CourseRecord courseRecord;
    private CourseRecords courseRecords;
    private Course course;
    private ModuleRecord moduleRecord;
    private UserDetailsDto input;
    private LaunchLink launchLink;

    @PostConstruct
    public void populateTestData() {
        courseId = testDataService.getCourseId();
        userId = testDataService.getUserId();
        moduleId = testDataService.getModuleId();
        courseRecord = testDataService.generateCourseRecord(true);
        courseRecords = new CourseRecords(List.of(courseRecord));
        moduleRecord = courseRecord.getModuleRecord(moduleId).get();
        course = testDataService.generateCourse(true, false);
        input = testDataService.generateUserDetailsDto();
        launchLink = new LaunchLink("http://launch.link");
    }

    @Test
    public void testGetELearningLaunchLinkUidExists() throws Exception {
        String expectedCourseRecordPUT = """
                [{
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "state" : "IN_PROGRESS",
                    "modules": [
                        {
                            "id" : 1,
                            "moduleId" : "moduleId",
                            "moduleTitle" : "Test Module",
                            "state": "IN_PROGRESS"
                        }
                    ]
                }]
                """;
        moduleRecord.setState(State.IN_PROGRESS);
        cslStubService.stubUpdateCourseRecord(courseId, course, userId, courseRecords, expectedCourseRecordPUT, courseRecord);
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
        moduleRecord.setUid(null);
        course.getModule(moduleId).setModuleType(ModuleType.file);
        course.getModule(moduleId).setUrl("http://launch.link");
        cslStubService.getLearningCatalogue().getCourse(courseId, course);
        cslStubService.getLearnerRecord().getCourseRecord(courseId, userId, courseRecords);
        String expectedCourseRecordPOST = """
                [{
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "state" : "COMPLETED",
                    "modules": [
                        {
                            "id" : 1,
                            "moduleId" : "moduleId",
                            "moduleTitle" : "Test Module",
                            "state": "COMPLETED"
                        }
                    ]
                }]
                """;
        cslStubService.getLearnerRecord().updateCourseRecords(expectedCourseRecordPOST, new CourseRecords(courseRecord));
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
        cslStubService.getLearnerRecord().getCourseRecord(courseId, userId, new CourseRecords());
        String expectedCourseRecordPOST = """
                [{
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "state" : "COMPLETED",
                    "modules": [
                        {
                            "id" : null,
                            "moduleId" : "moduleId",
                            "moduleTitle" : "Test Module",
                            "state": "COMPLETED",
                            "completionDate" : "2023-01-01T10:00:00"
                        }
                    ]
                }]
                """;
        cslStubService.getLearnerRecord().createCourseRecord(expectedCourseRecordPOST, new CourseRecords(courseRecord));
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
    }

    @Test
    public void testLaunchNewCourse() throws Exception {
        course.getModule(moduleId).setModuleType(ModuleType.file);
        course.getModule(moduleId).setUrl("http://launch.link");
        String expectedCourseRecordPOST = """
                [{
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "state" : "COMPLETED",
                    "modules": [
                        {
                            "id" : null,
                            "moduleId" : "moduleId",
                            "moduleTitle" : "Test Module",
                            "state": "COMPLETED"
                        }
                    ]
                }]
                """;
        cslStubService.stubCreateCourseRecord(courseId, course, userId, expectedCourseRecordPOST, courseRecord);

        String url = String.format("/courses/%s/modules/%s/launch", courseId, moduleId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utils.toJson(input)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.launchLink").value("http://launch.link"));
    }

}
