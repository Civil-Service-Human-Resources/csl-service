package uk.gov.cabinetoffice.csl.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
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
        CourseRecord inProgressCourseRecord = testDataService.generateCourseRecord(false);
        inProgressCourseRecord.setState(State.IN_PROGRESS);
        CourseRecords courseRecords = new CourseRecords(List.of(inProgressCourseRecord));
        CourseRecord archivedCourseRecord = testDataService.generateCourseRecord(false);
        archivedCourseRecord.setState(State.ARCHIVED);
        String expectedCourseRecordPUT = """
                [{
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "state" : "ARCHIVED"
                }]
                """;
        cslStubService.stubUpdateCourseRecord(courseId, course, userId, courseRecords, expectedCourseRecordPUT, archivedCourseRecord);
        String url = String.format("/courses/%s/remove_from_learning_plan", courseId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.courseId").value("courseId"))
                .andExpect(jsonPath("$.courseTitle").value(testDataService.getCourseTitle()))
                .andExpect(jsonPath("$.message").value("Successfully applied action 'Remove from learning plan' to course record"));
        ;

    }

    @Test
    public void TestRemoveCourseFromLearningPlanCourseRecordNotFound() throws Exception {
        String courseId = testDataService.getCourseId();
        String userId = testDataService.getUserId();
        cslStubService.getLearningCatalogue().getCourse(courseId, this.testDataService.generateCourse(false, false));
        cslStubService.getLearnerRecord().getCourseRecord(courseId, userId, new CourseRecords());
        String url = String.format("/courses/%s/remove_from_learning_plan", courseId);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.title").value("Record is in the incorrect state"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.instance").value("/courses/courseId/remove_from_learning_plan"));
        ;
    }
}
