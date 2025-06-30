package uk.gov.cabinetoffice.csl.integration;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.ArrayList;
import java.util.List;

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
                        "createdTimestamp" : "2023-02-02T10:00:00",
                        "events" : [{
                            "learnerId": "userId",
                            "resourceId": "courseId",
                            "eventType": "COMPLETE_COURSE",
                            "eventTimestamp" : "2023-02-02T10:00:00",
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
                        "createdTimestamp" : "2023-02-02T10:00:00",
                        "events" : [{
                            "learnerId": "userId",
                            "resourceId": "courseId",
                            "eventType": "COMPLETE_COURSE",
                            "eventTimestamp" : "2023-02-02T10:00:00",
                            "eventSource": {"source": "csl_source_id"}
                        }]
                    }],
                    "failedResources": []
                }
                """;
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
        cslStubService.getLearnerRecord().createLearnerRecords(expectedLearnerRecordsPOST, expectedLearnerRecordsPOSTResponse);
        mockMvc.perform(post("/rustici/rollup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utils.toJson(rollupData)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testRollupCompleteBlended() throws Exception {
        List<StubMapping> stubs = new ArrayList<>();
        Course testCourse = testDataService.generateCourse(2);
        String module0 = "moduleId0";
        testCourse.getModule(module0).setModuleType(ModuleType.file);
        testCourse.getModule(module0).setUrl("http://launch.link");
        String module1 = "moduleId1";
        testCourse.getModule(module1).setModuleType(ModuleType.elearning);

        CivilServant civilServant = testDataService.generateCivilServant();
        stubs.add(cslStubService.stubGetUserDetails(testDataService.getUserId(), civilServant));

        uk.gov.cabinetoffice.csl.domain.rustici.Course rusticiCourse = new uk.gov.cabinetoffice.csl.domain.rustici.Course();
        rusticiCourse.setId(String.format("%s.%s", testCourse.getId(), module1));
        RusticiRollupData blendedRollup = testDataService.generateRusticiRollupData();
        blendedRollup.setCourse(rusticiCourse);

        stubs.add(cslStubService.getLearnerRecord().getLearnerRecords("userId", "courseId", 0, """
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
                                    "eventType": "MOVE_TO_LEARNING_PLAN",
                                    "learnerRecordType": {
                                        "type": "COURSE"
                                    }
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
                """));
        String expectedLearnerRecordsPOST = """
                [{
                    "learnerId": "userId",
                    "resourceId": "courseId",
                    "eventType": "COMPLETE_COURSE",
                    "eventTimestamp" : "2023-02-02T10:00:00",
                    "eventSource": "csl_source_id"
                }]
                """;
        String expectedLearnerRecordsPOSTResponse = """
                {
                    "successfulResources": [{
                        "learnerId": "userId",
                        "resourceId": "courseId",
                        "eventType": {
                            "eventType": "COMPLETE_COURSE",
                            "learnerRecordType": {
                                "type": "COURSE"
                            }
                        },
                        "eventTimestamp" : "2023-02-02T10:00:00",
                        "eventSource": {"source": "csl_source_id"}
                    }],
                    "failedResources": []
                }
                """;
        String expectedModuleRecordGET = """
                {"moduleRecords": [{
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId0",
                    "moduleTitle" : "Test Module",
                    "state": "COMPLETED",
                    "completionDate" : "2023-01-01T10:00:00"
                },{
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId1",
                    "moduleTitle" : "Test Module",
                    "state": "IN_PROGRESS"
                }]}
                """;
        String expectedModuleRecordPUT = """
                [{
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId1",
                    "moduleTitle" : "Test Module",
                    "state": "COMPLETED",
                    "completionDate" : "2023-02-02T10:00:00"
                }]
                """;
        String expectedModuleRecordPUTResponse = """
                {"moduleRecords":[{
                    "userId": "userId",
                    "courseId": "courseId",
                    "moduleId" : "moduleId1",
                    "moduleTitle" : "Test Module",
                    "state": "COMPLETED",
                    "completionDate" : "2023-02-02T10:00:00"
                }]}
                """;
        stubs.add(cslStubService.getLearningCatalogue().getCourse(testCourse));
        stubs.add(cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("moduleId0", "moduleId1"), expectedModuleRecordGET));
        stubs.add(cslStubService.getLearnerRecord().updateModuleRecords(expectedModuleRecordPUT, expectedModuleRecordPUTResponse));
        stubs.add(cslStubService.getLearnerRecord().createLearnerRecordEvent(expectedLearnerRecordsPOST, expectedLearnerRecordsPOSTResponse));
        mockMvc.perform(post("/rustici/rollup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utils.toJson(blendedRollup)))
                .andExpect(status().is2xxSuccessful());
        cslStubService.assertStubbedRequests(stubs);
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
