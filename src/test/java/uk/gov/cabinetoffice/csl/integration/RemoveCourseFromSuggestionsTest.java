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
public class RemoveCourseFromSuggestionsTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CSLStubService cslStubService;

    @Test
    public void TestRemoveCourseFromSuggestions() throws Exception {
        String courseId = testDataService.getCourseId();
        String userId = testDataService.getUserId();
        Course course = testDataService.generateCourse(true, false);
        String expectedLearnerRecordGET = """
                {
                    "content": [],
                    "totalPages": 0
                }
                """;
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
                            "eventType": "REMOVE_FROM_SUGGESTIONS",
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
                            "eventType": "REMOVE_FROM_SUGGESTIONS",
                            "eventTimestamp" : "2023-01-01T10:00:00",
                            "eventSource": {"source": "csl_source_id"}
                        }]
                    }],
                    "failedResources": []
                }
                """;
        cslStubService.getLearningCatalogue().getCourses(List.of(courseId), List.of(course));
        cslStubService.getLearnerRecord().getLearnerRecords(userId, courseId, 0, expectedLearnerRecordGET);
        cslStubService.getLearnerRecord().createLearnerRecords(expectedLearnerRecordsPOST, expectedLearnerRecordsPOSTResponse);
        String url = String.format("/courses/%s/remove_from_suggestions", courseId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.courseId").value(courseId))
                .andExpect(jsonPath("$.courseTitle").value(testDataService.getCourseTitle()))
                .andExpect(jsonPath("$.message").value("Successfully applied action 'Remove from suggestions' to COURSE courseId (Test Course)"));

    }

}
