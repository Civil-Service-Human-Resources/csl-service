package uk.gov.cabinetoffice.csl.integration.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEventQuery;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.data.ArrayJsonContentBuilder;
import uk.gov.cabinetoffice.csl.util.data.catalogue.DateRangeJsonValues;
import uk.gov.cabinetoffice.csl.util.data.catalogue.JsonCourseBuilder;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LearningRecordTest extends IntegrationTestBase {

    @Autowired
    private CSLStubService cslStubService;

    @Autowired
    private TestDataService testDataService;

    private CivilServant civilServant;

    String requiredLearningMap = """
            {
                "departmentCodeMap": {
                    "CO": ["course1", "course2"]
                }
            }
            """;

    JsonCourseBuilder course1 = JsonCourseBuilder.create("course1", "Course 1")
            .addLinkModule("module1", "module1", false, 30)
            .addDepartmentRequiredLearning("DWP", "2024-01-01T00:00:00Z", "P1Y")
            .addDepartmentRequiredLearning("HMRC", "2023-01-01T00:00:00Z", "P1Y");

    JsonCourseBuilder course2 = JsonCourseBuilder.create("course2", "Course 2")
            .addLinkModule("module1", "module1", false, 0)
            .addFileModule("module2", "module2", false, 0)
            .addDepartmentRequiredLearning("CO", "2024-01-01T00:00:00Z", "P1Y");

    JsonCourseBuilder course3 = JsonCourseBuilder.create("course3", "Course 3")
            .addFaceToFaceModule("module1", "module1", false, 0, "eventId", BigDecimal.valueOf(0L), new DateRangeJsonValues("09:00", "11:00", "2025-01-01"))
            .createBlankAudience();

    String courses = ArrayJsonContentBuilder.create(course1, course2, course3).build();

    @Test
    public void testGetLearningRecord() throws Exception {
        String eventsResponse = """
                {
                    "content": [
                        {
                            "eventTimestamp": "2022-01-01T00:00:00Z",
                            "resourceId": "course1"
                        },
                        {
                            "eventTimestamp": "2023-01-01T00:00:00Z",
                            "resourceId": "course1"
                        },
                        {
                            "eventTimestamp": "2022-01-01T00:00:00Z",
                            "resourceId": "course2"
                        },
                        {
                            "eventTimestamp": "2023-01-01T00:00:00Z",
                            "resourceId": "course3"
                        }
                    ],
                    "totalPages": 1
                }
                """;

        cslStubService.getLearningCatalogue().getMandatoryLearningMap(requiredLearningMap);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1", "course2", "course3"), courses);
        cslStubService.getCsrsStubService().getCivilServant("userId", testDataService.generateCivilServant());
        cslStubService.getLearnerRecord().getLearnerRecordEvents(0, LearnerRecordEventQuery.builder().userId("userId")
                .eventTypes(List.of("COMPLETE_COURSE")).build(), eventsResponse);
        mockMvc.perform(get("/learning/record")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.userId").value("userId"))
                .andExpect(jsonPath("$.requiredLearningRecord.totalRequired").value(2))
                .andExpect(jsonPath("$.requiredLearningRecord.completedCourses.length()").value(1))
                .andExpect(jsonPath("$.requiredLearningRecord.completedCourses[0].id").value("course1"))
                .andExpect(jsonPath("$.requiredLearningRecord.completedCourses[0].title").value("Course 1"))
                .andExpect(jsonPath("$.requiredLearningRecord.completedCourses[0].type").value("link"))
                .andExpect(jsonPath("$.requiredLearningRecord.completedCourses[0].duration").value(30))
                .andExpect(jsonPath("$.requiredLearningRecord.completedCourses[0].completionDate").value("2023-01-01T00:00:00"))
                .andExpect(jsonPath("$.otherLearning[0].id").value("course3"))
                .andExpect(jsonPath("$.otherLearning[0].title").value("Course 3"))
                .andExpect(jsonPath("$.otherLearning[0].type").value("face-to-face"))
                .andExpect(jsonPath("$.otherLearning[0].duration").value(7200))
                .andExpect(jsonPath("$.otherLearning[0].completionDate").value("2023-01-01T00:00:00"));

    }
}
