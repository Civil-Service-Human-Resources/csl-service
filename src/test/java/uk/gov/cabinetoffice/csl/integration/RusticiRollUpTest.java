package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class RusticiRollUpTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CSLStubService cslStubService;
    RusticiRollupData rollupData;

    @PostConstruct
    public void setupData() {
        rollupData = testDataService.generateRusticiRollupData();
    }


    @Test
    public void testRollUpCompletedModuleRecord() throws Exception {
        Course course = testDataService.generateCourse(true, false);
        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.stubGetUserDetails(testDataService.getUserId(), civilServant);
        String getModuleRecordsResponse = """
                {"moduleRecords": [{
                    "id" : 1,
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
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "COMPLETED",
                    "completionDate": "2023-02-02T10:00:00"
                }]
                """;
        String expectedModuleRecordPUTResponse = """
                {"moduleRecords":[{
                    "id" : 1,
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "COMPLETED",
                    "completionDate": "2023-02-02T10:00:00"
                }]}
                """;
        cslStubService.stubUpdateModuleRecord(course, testDataService.getModuleId(), testDataService.getUserId(),
                getModuleRecordsResponse, expectedModuleRecordPUT, expectedModuleRecordPUTResponse);
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
        String getModuleRecordsResponse = """
                {"moduleRecords": [{
                    "id" : 1,
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
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "IN_PROGRESS",
                    "result": "FAILED"
                }]
                """;
        String expectedModuleRecordPUTResponse = """
                {"moduleRecords":[{
                    "id" : 1,
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId",
                    "moduleTitle" : "Test Module",
                    "state": "IN_PROGRESS",
                    "result": "FAILED"
                }]}
                """;
        cslStubService.stubUpdateModuleRecord(course, testDataService.getModuleId(), testDataService.getUserId(),
                getModuleRecordsResponse, expectedModuleRecordPUT, expectedModuleRecordPUTResponse);
        mockMvc.perform(post("/rustici/rollup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utils.toJson(failedRollUpData)))
                .andExpect(status().is2xxSuccessful());
    }

}
