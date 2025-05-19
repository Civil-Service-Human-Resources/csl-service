package uk.gov.cabinetoffice.csl.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class RemoveCourseFromLearningPlanTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CSLStubService cslStubService;

    @Test
    public void TestRemoveCourseFromLearningPlan() throws Exception {
        Course course = this.testDataService.generateCourse(false, false);
        String courseId = testDataService.getCourseId();
        String userId = testDataService.getUserId();
        String expectedLearnerRecordGET = """
                {
                    "content": [
                        {
                            "resourceId": "courseId",
                            "learnerId": "userId",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": {
                                "learnerId": "userId",
                                "resourceId": "courseId",
                                "eventType": {
                                    "type": "ADD_TO_LEARNING_PLAN"
                                },
                                "eventTimestamp" : "2023-01-01T10:00:00",
                                "eventSource": {
                                    "source": "csl_source_id"
                                }
                            }
                        }
                    ],
                    "totalPages": 1
                }
                """;
        String expectedEventPOST = """
                [
                    {
                        "learnerId": "userId",
                        "resourceId": "courseId",
                        "eventType": "REMOVE_FROM_LEARNING_PLAN",
                        "eventTimestamp": "2023-01-01T10:00:00",
                        "eventSource": "csl_source_id"
                    }
                ]
                """;
        String createEventResponse = """
                {
                    "successfulResources": [{
                        "learnerId": "userId",
                        "resourceId": "courseId",
                        "eventType": {
                            "type": "REMOVE_FROM_LEARNING_PLAN"
                        },
                        "eventTimestamp" : "2023-01-01T10:00:00",
                        "eventSource": {"source": "csl_source_id"}
                    }],
                    "failedResources": []
                }
                """;
        cslStubService.getLearningCatalogue().getCourses(List.of(courseId), List.of(course));
        cslStubService.getLearnerRecord().getLearnerRecords(userId, courseId, 0, expectedLearnerRecordGET);
        cslStubService.getLearnerRecord().createLearnerRecordEvent(expectedEventPOST, createEventResponse);
        String url = String.format("/courses/%s/remove_from_learning_plan", courseId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.courseId").value("courseId"))
                .andExpect(jsonPath("$.courseTitle").value(testDataService.getCourseTitle()))
                .andExpect(jsonPath("$.message").value("Successfully applied action 'Remove from learning plan' to COURSE courseId (Test Course)"));

    }

    @Test
    public void TestRemoveCourseFromLearningPlanCourseRecordNotFound() throws Exception {
        String courseId = testDataService.getCourseId();
        String userId = testDataService.getUserId();
        cslStubService.getLearningCatalogue().getCourse(courseId, this.testDataService.generateCourse(false, false));
        cslStubService.getLearnerRecord().getLearnerRecords(userId, courseId, 0, """
                {
                    "content": [],
                    "totalPages": 0
                }
                """);
        String url = String.format("/courses/%s/remove_from_learning_plan", courseId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.courseId").value("courseId"))
                .andExpect(jsonPath("$.courseTitle").value(testDataService.getCourseTitle()))
                .andExpect(jsonPath("$.message").value("Did not apply action 'Remove from learning plan' to COURSE courseId (Test Course)"));
    }
}
