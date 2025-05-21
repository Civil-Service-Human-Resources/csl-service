package uk.gov.cabinetoffice.csl.integration.courseRecord;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CourseRecordTest extends IntegrationTestBase {

    @Autowired
    private CSLStubService cslStubService;

    @Autowired
    private TestDataService testDataService;

    @Test
    public void getCourseRecords() throws Exception {
        String courseRecord = """
                {
                    "content": [
                        {
                            "resourceId": "courseId",
                            "learnerId": "userId",
                            "recordType": {
                                "type": "COURSE"
                            }
                        }
                    ],
                    "totalPages": 1
                }
                """;
        String moduleRecords = """
                {
                    "moduleRecords": [
                        {
                            "id": 1,
                            "uid": "module1",
                            "userId": "userId",
                            "courseId": "courseId",
                            "moduleId": "module1",
                            "moduleTitle": "module1",
                            "moduleType": "link",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2023-06-01T10:00:00",
                            "createdAt": "2023-06-01T10:00:00",
                            "updatedAt": "2023-06-01T10:00:00"
                        },
                        {
                            "id": 2,
                            "uid": "module2",
                            "userId": "userId",
                            "courseId": "courseId",
                            "moduleId": "module2",
                            "moduleTitle": "module2",
                            "moduleType": "elearning",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2023-06-01T10:00:00",
                            "createdAt": "2023-06-01T10:00:00",
                            "updatedAt": "2023-06-01T10:00:00"
                        }
                    ]
                }
                """;
        Course course = testDataService.generateCourse(2);
        cslStubService.getLearningCatalogue().getCourses(List.of(course.getId()), List.of(course));
        cslStubService.getLearnerRecord().getLearnerRecords("userId", 0, courseRecord);
        cslStubService.getLearnerRecord().getModuleRecordsForUser("userId", moduleRecords);
        mockMvc.perform(get("/course_records")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.courseRecords.length()").value(1))
                .andExpect(jsonPath("$.courseRecords[0].courseId").value("courseId"))
                .andExpect(jsonPath("$.courseRecords[0].userId").value("userId"))
                .andExpect(jsonPath("$.courseRecords[0].courseTitle").value("Test Course"))
                .andExpect(jsonPath("$.courseRecords[0].state").value("IN_PROGRESS"))

                .andExpect(jsonPath("$.courseRecords[0].modules.length()").value(2))

                .andExpect(jsonPath("$.courseRecords[0].modules[0].id").value(1))
                .andExpect(jsonPath("$.courseRecords[0].modules[0].uid").value("module1"))
                .andExpect(jsonPath("$.courseRecords[0].modules[0].moduleId").value("module1"))
                .andExpect(jsonPath("$.courseRecords[0].modules[0].moduleType").value("link"))
                .andExpect(jsonPath("$.courseRecords[0].modules[0].state").value("COMPLETED"))

                .andExpect(jsonPath("$.courseRecords[0].modules[1].id").value(2))
                .andExpect(jsonPath("$.courseRecords[0].modules[1].uid").value("module2"))
                .andExpect(jsonPath("$.courseRecords[0].modules[1].moduleId").value("module2"))
                .andExpect(jsonPath("$.courseRecords[0].modules[1].moduleType").value("elearning"))
                .andExpect(jsonPath("$.courseRecords[0].modules[1].state").value("COMPLETED"));
    }


}
