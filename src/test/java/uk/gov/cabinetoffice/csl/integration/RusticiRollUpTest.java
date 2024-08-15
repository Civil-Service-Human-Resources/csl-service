package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class RusticiRollUpTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CSLStubService cslStubService;

    CourseRecord courseRecord;
    CourseRecords courseRecords;
    RusticiRollupData rollupData;

    @PostConstruct
    public void setupData() {
        courseRecord = testDataService.generateCourseRecord(true);
        courseRecords = new CourseRecords(List.of(courseRecord));
        rollupData = testDataService.generateRusticiRollupData();
    }


    @Test
    public void testRollUpCompletedModuleRecord() throws Exception {
        Course course = testDataService.generateCourse(true, false);
        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.stubGetUserDetails(testDataService.getUserId(), civilServant);
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
        cslStubService.stubUpdateCourseRecord(testDataService.getCourseId(), course, testDataService.getUserId(),
                courseRecords, expectedCourseRecordPUT, courseRecord);
        mockMvc.perform(post("/rustici/rollup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utils.toJson(rollupData)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testRollUpFailedModuleRecord() throws Exception {
        Course course = testDataService.generateCourse(true, false);
        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.stubGetUserDetails(testDataService.getUserId(), civilServant);
        RusticiRollupData failedRollUpData = testDataService.generateRusticiRollupData();
        failedRollUpData.setRegistrationSuccess("FAILED");
        failedRollUpData.setCompletedDate(null);
        String expectedCourseRecordPUT = """
                {
                    "courseId" : "courseId",
                    "userId" : "userId",
                    "courseTitle" : "Test Course",
                    "state" : "NULL",
                    "modules": [
                        {
                            "id" : 1,
                            "moduleId" : "moduleId",
                            "moduleTitle" : "Test Module",
                            "state": "NULL",
                            "result": "FAILED"
                        }
                    ]
                }
                """;
        cslStubService.stubUpdateCourseRecord(testDataService.getCourseId(), course, testDataService.getUserId(),
                courseRecords, expectedCourseRecordPUT, courseRecord);
        mockMvc.perform(post("/rustici/rollup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utils.toJson(failedRollUpData)))
                .andExpect(status().is2xxSuccessful());
    }

}
