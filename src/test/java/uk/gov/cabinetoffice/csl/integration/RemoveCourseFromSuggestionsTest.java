package uk.gov.cabinetoffice.csl.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

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
        CourseRecord courseRecord = testDataService.generateCourseRecord(false);
        String courseId = testDataService.getCourseId();
        String userId = testDataService.getUserId();
        Course course = testDataService.generateCourse(true, false);
        String expectedCourseRecordPOST = """
                [{
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "preference": "DISLIKED",
                    "state": null
                }]
                """;
        cslStubService.stubCreateCourseRecord(courseId, course, userId, expectedCourseRecordPOST, courseRecord);
        String url = String.format("/courses/%s/remove_from_suggestions", courseId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.courseId").value(courseId))
                .andExpect(jsonPath("$.courseTitle").value(testDataService.getCourseTitle()))
                .andExpect(jsonPath("$.message").value("Successfully applied action 'Remove from suggestions' to course record"));

    }

}
