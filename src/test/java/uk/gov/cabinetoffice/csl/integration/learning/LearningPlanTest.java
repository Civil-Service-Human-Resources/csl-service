package uk.gov.cabinetoffice.csl.integration.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.data.ArrayJsonContentBuilder;
import uk.gov.cabinetoffice.csl.util.data.catalogue.DateRangeJsonValues;
import uk.gov.cabinetoffice.csl.util.data.catalogue.JsonCourseBuilder;
import uk.gov.cabinetoffice.csl.util.data.learnerRecord.JsonLearnerRecordBuilder;
import uk.gov.cabinetoffice.csl.util.data.learnerRecord.JsonModuleRecordBuilder;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LearningPlanTest extends IntegrationTestBase {

    @Autowired
    private CSLStubService cslStubService;

    @Autowired
    private TestDataService testDataService;

    String requiredLearningMap = """
            {
                "departmentCodeMap": {
                    "CO": ["course1", "course2"]
                }
            }
            """;

    JsonCourseBuilder course3 = JsonCourseBuilder.create("course3", "Course 3")
            .addLinkModule("module1", "Module 1", false, 30);

    JsonCourseBuilder course4 = JsonCourseBuilder.create("course4", "Course 4")
            .addLinkModule("module2", "Module 2", false, 0)
            .addFileModule("module3", "Module 3", false, 0);

    JsonCourseBuilder course5 = JsonCourseBuilder.create("course5", "Course 5")
            .addFaceToFaceModule("module4", "Module 4", false, 0, "event1", BigDecimal.valueOf(0L),
                    new DateRangeJsonValues("12:00", "14:00", "2022-01-02"),
                    new DateRangeJsonValues("09:00", "11:00", "2022-01-01"));

    JsonCourseBuilder course6 = JsonCourseBuilder.create("course6", "Course 6")
            .addElearningModule("module5", "Module 5", false, 0);

    JsonCourseBuilder course7 = JsonCourseBuilder.create("course7", "Course 7")
            .addElearningModule("module6", "Module 6", false, 0);

    JsonCourseBuilder course8 = JsonCourseBuilder.create("course8", "Course 8")
            .addFaceToFaceModule("module7", "Module 7", false, 0, "event2", BigDecimal.valueOf(0L),
                    new DateRangeJsonValues("12:00", "14:00", "2022-01-02"),
                    new DateRangeJsonValues("09:00", "11:00", "2022-01-01"))
            .addElearningModule("module8", "Module 8", false, 0);

    String courses = ArrayJsonContentBuilder.create(course3, course4, course5, course6, course7, course8).build();

    @Test
    public void testGetLearningPlan() throws Exception {

        String recordResponse = ArrayJsonContentBuilder.create(
                JsonLearnerRecordBuilder.create("userId", "course2").addLatestEvent("MOVE_TO_LEARNING_PLAN", "2023-01-01T10:00:00"),
                JsonLearnerRecordBuilder.create("userId", "course3").addLatestEvent("MOVE_TO_LEARNING_PLAN", "2023-01-01T10:00:00"),
                JsonLearnerRecordBuilder.create("userId", "course4").addLatestEvent("MOVE_TO_LEARNING_PLAN", "2023-01-01T10:00:00"),
                JsonLearnerRecordBuilder.create("userId", "course5").addLatestEvent("MOVE_TO_LEARNING_PLAN", "2023-01-01T10:00:00"),
                JsonLearnerRecordBuilder.create("userId", "course6").addLatestEvent("REMOVE_FROM_LEARNING_PLAN", "2025-01-01T10:00:00"),
                JsonLearnerRecordBuilder.create("userId", "course7"),
                JsonLearnerRecordBuilder.create("userId", "course8")
        ).getAsPaginated(0, 20, 1).toString();

        String moduleRecordResponse = ArrayJsonContentBuilder.create(
                JsonModuleRecordBuilder.create("module2", "course4", "userId", "link", "2025-01-01T09:00:00")
                        .addUpdatedAt("2025-01-01T09:00:00").addState("IN_PROGRESS"),
                JsonModuleRecordBuilder.create("module4", "course5", "userId", "link", "2022-01-02T09:00:00")
                        .addUpdatedAt("2022-01-02T09:00:00").addState("APPROVED").addEvent("event1", "2022-01-02T09:00:00"),
                JsonModuleRecordBuilder.create("module5", "course6", "userId", "link", "2025-01-01T09:00:00")
                        .addUpdatedAt("2025-01-01T09:00:00").addState("IN_PROGRESS"),
                JsonModuleRecordBuilder.create("module6", "course7", "userId", "link", "2025-01-01T09:00:00")
                        .addUpdatedAt("2025-01-01T09:00:00").addState("IN_PROGRESS"),
                JsonModuleRecordBuilder.create("module7", "course8", "userId", "link", "2022-01-01T09:00:00")
                        .addUpdatedAt("2022-01-01T09:00:00").addState("APPROVED").addEvent("event2", "2022-01-01T09:00:00")
        ).getAsObjectList("moduleRecords").toString();

        cslStubService.getLearningCatalogue().getMandatoryLearningMap(requiredLearningMap);
        cslStubService.getLearningCatalogue().getCourses(List.of("course3", "course4", "course5", "course6", "course7", "course8"), courses);
        cslStubService.getCsrsStubService().getCivilServant("userId", testDataService.generateCivilServant());
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1", "module2", "module3", "module5", "module6", "module7", "module8"), moduleRecordResponse);
        cslStubService.getLearnerRecord().getLearnerRecords(LearnerRecordQuery.builder().notEventTypes(List.of("COMPLETE_COURSE")).learnerRecordTypes(List.of("COURSE")).build(), 0, recordResponse);

        String expectedJson = """
                {
                  "userId": "userId",
                  "bookedCourses": [
                  {
                      "id": "course5",
                      "title": "Course 5",
                      "shortDescription": "Course 5 short description",
                      "type": "face-to-face",
                      "duration": 14400,
                      "moduleCount": 1,
                      "costInPounds": 0,
                      "status": "NULL",
                      "eventModule": {
                        "id": "module4",
                        "title": "Module 4",
                        "eventId": "event1",
                        "bookedDate": "2022-01-02",
                        "dates": ["2022-01-01", "2022-01-02"],
                        "state": "APPROVED"
                      },
                      "canBeMovedToLearningRecord": true
                    },
                  {
                       "id": "course8",
                       "title": "Course 8",
                       "shortDescription": "Course 8 short description",
                       "type": "blended",
                       "duration": 14400,
                       "moduleCount": 2,
                       "costInPounds": 0,
                       "status": "NULL",
                       "eventModule": {
                         "id": "module7",
                         "title": "Module 7",
                         "eventId": "event2",
                         "bookedDate": "2022-01-01",
                         "dates": ["2022-01-01", "2022-01-02"],
                         "state": "APPROVED"
                       },
                       "canBeMovedToLearningRecord": false
                     }
                  ],
                  "learningPlanCourses": [
                    {
                      "id": "course4",
                      "title": "Course 4",
                      "shortDescription": "Course 4 short description",
                      "type": "blended",
                      "duration": 0,
                      "moduleCount": 2,
                      "costInPounds": 0,
                      "status": "IN_PROGRESS"
                    },
                    {
                      "id": "course3",
                      "title": "Course 3",
                      "shortDescription": "Course 3 short description",
                      "type": "link",
                      "duration": 30,
                      "moduleCount": 1,
                      "costInPounds": 0,
                      "status": "NULL"
                    },
                    {
                      "id": "course7",
                      "title": "Course 7",
                      "shortDescription": "Course 7 short description",
                      "type": "elearning",
                      "duration": 0,
                      "moduleCount": 1,
                      "costInPounds": 0,
                      "status": "IN_PROGRESS"
                    }
                  ]
                }""";

        mockMvc.perform(get("/learning/plan")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(expectedJson, true));

    }

    @Test
    public void testGetLearningPlanWhenHomepageCompleteLearningPlanCoursesIsTrue() throws Exception {

        String recordResponse = ArrayJsonContentBuilder.create(
                JsonLearnerRecordBuilder.create("userId", "course2").addLatestEvent("MOVE_TO_LEARNING_PLAN", "2023-01-01T10:00:00"),
                JsonLearnerRecordBuilder.create("userId", "course3").addLatestEvent("MOVE_TO_LEARNING_PLAN", "2023-01-01T10:00:00"),
                JsonLearnerRecordBuilder.create("userId", "course4").addLatestEvent("MOVE_TO_LEARNING_PLAN", "2023-01-01T10:00:00"),
                JsonLearnerRecordBuilder.create("userId", "course5").addLatestEvent("MOVE_TO_LEARNING_PLAN", "2023-01-01T10:00:00"),
                JsonLearnerRecordBuilder.create("userId", "course6").addLatestEvent("REMOVE_FROM_LEARNING_PLAN", "2025-01-01T10:00:00"),
                JsonLearnerRecordBuilder.create("userId", "course7"),
                JsonLearnerRecordBuilder.create("userId", "course8")
        ).getAsPaginated(0, 20, 1).toString();

        String moduleRecordResponse = ArrayJsonContentBuilder.create(
                JsonModuleRecordBuilder.create("module2", "course4", "userId", "link", "2025-01-01T09:00:00")
                        .addUpdatedAt("2025-01-01T09:00:00").addState("IN_PROGRESS"),
                JsonModuleRecordBuilder.create("module4", "course5", "userId", "link", "2022-01-02T09:00:00")
                        .addUpdatedAt("2022-01-02T09:00:00").addState("APPROVED").addEvent("event1", "2022-01-02T09:00:00"),
                JsonModuleRecordBuilder.create("module5", "course6", "userId", "link", "2025-01-01T09:00:00")
                        .addUpdatedAt("2025-01-01T09:00:00").addState("IN_PROGRESS"),
                JsonModuleRecordBuilder.create("module6", "course7", "userId", "link", "2025-01-01T09:00:00")
                        .addUpdatedAt("2025-01-01T09:00:00").addState("IN_PROGRESS"),
                JsonModuleRecordBuilder.create("module7", "course8", "userId", "link", "2022-01-01T09:00:00")
                        .addUpdatedAt("2022-01-01T09:00:00").addState("APPROVED").addEvent("event2", "2022-01-01T09:00:00")
        ).getAsObjectList("moduleRecords").toString();

        cslStubService.getLearningCatalogue().getMandatoryLearningMap(requiredLearningMap);
        cslStubService.getLearningCatalogue().getCourses(List.of("course3", "course4", "course5", "course6", "course7", "course8"), courses);
        cslStubService.getCsrsStubService().getCivilServant("userId", testDataService.generateCivilServant());
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1", "module2", "module3", "module5", "module6", "module7", "module8"), moduleRecordResponse);
        cslStubService.getLearnerRecord().getLearnerRecords(LearnerRecordQuery.builder().notEventTypes(List.of("COMPLETE_COURSE")).learnerRecordTypes(List.of("COURSE")).build(), 0, recordResponse);

        String expectedJson = """
                {
                  "userId": "userId",
                  "bookedCourses": [
                  {
                      "id": "course5",
                      "title": "Course 5",
                      "shortDescription": "Course 5 short description",
                      "type": "face-to-face",
                      "duration": 14400,
                      "moduleCount": 1,
                      "costInPounds": 0,
                      "status": "NULL",
                      "eventModule": {
                        "id": "module4",
                        "title": "Module 4",
                        "eventId": "event1",
                        "bookedDate": "2022-01-02",
                        "dates": ["2022-01-01", "2022-01-02"],
                        "state": "APPROVED"
                      },
                      "canBeMovedToLearningRecord": true
                    },
                  {
                       "id": "course8",
                       "title": "Course 8",
                       "shortDescription": "Course 8 short description",
                       "type": "blended",
                       "duration": 14400,
                       "moduleCount": 2,
                       "costInPounds": 0,
                       "status": "NULL",
                       "eventModule": {
                         "id": "module7",
                         "title": "Module 7",
                         "eventId": "event2",
                         "bookedDate": "2022-01-01",
                         "dates": ["2022-01-01", "2022-01-02"],
                         "state": "APPROVED"
                       },
                       "canBeMovedToLearningRecord": false
                     }
                  ],
                  "learningPlanCourses": [
                    {
                      "id": "course4",
                      "title": "Course 4",
                      "shortDescription": "Course 4 short description",
                      "type": "blended",
                      "duration": 0,
                      "moduleCount": 2,
                      "costInPounds": 0,
                      "status": "IN_PROGRESS"
                    },
                    {
                      "id": "course3",
                      "title": "Course 3",
                      "shortDescription": "Course 3 short description",
                      "type": "link",
                      "duration": 30,
                      "moduleCount": 1,
                      "costInPounds": 0,
                      "status": "NULL"
                    },
                    {
                      "id": "course7",
                      "title": "Course 7",
                      "shortDescription": "Course 7 short description",
                      "type": "elearning",
                      "duration": 0,
                      "moduleCount": 1,
                      "costInPounds": 0,
                      "status": "IN_PROGRESS"
                    }
                  ]
                }""";

        mockMvc.perform(get("/learning/plan?HOMEPAGE_COMPLETE_LEARNING_PLAN_COURSES=true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(expectedJson, true));

    }

    @Test
    public void testGetLearningPlanWhenWhenAllModulesAreCompletedAndHomepageCompleteLearningPlanCoursesIsFalse() throws Exception {

        String courses47 = ArrayJsonContentBuilder.create(course4, course7).build();

        String recordResponse = ArrayJsonContentBuilder.create(
                JsonLearnerRecordBuilder.create("userId", "course4").addLatestEvent("MOVE_TO_LEARNING_PLAN", "2023-01-01T10:00:00"),
                JsonLearnerRecordBuilder.create("userId", "course7")
        ).getAsPaginated(0, 20, 1).toString();

        String moduleRecordResponse = ArrayJsonContentBuilder.create(
                JsonModuleRecordBuilder.create("module2", "course4", "userId", "link", "2025-01-01T09:00:00")
                        .addUpdatedAt("2025-01-01T09:00:00").addState("COMPLETED"),
                JsonModuleRecordBuilder.create("module3", "course5", "userId", "link", "2025-01-01T10:00:00")
                        .addUpdatedAt("2025-01-01T10:00:00").addState("COMPLETED"),
                JsonModuleRecordBuilder.create("module6", "course7", "userId", "link", "2025-01-01T09:00:00")
                        .addUpdatedAt("2025-01-01T09:00:00").addState("IN_PROGRESS")
        ).getAsObjectList("moduleRecords").toString();

        cslStubService.getLearningCatalogue().getMandatoryLearningMap(requiredLearningMap);
        cslStubService.getLearningCatalogue().getCourses(List.of("course4", "course7"), courses47);
        cslStubService.getCsrsStubService().getCivilServant("userId", testDataService.generateCivilServant());
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module2", "module3", "module6"), moduleRecordResponse);
        cslStubService.getLearnerRecord().getLearnerRecords(LearnerRecordQuery.builder().notEventTypes(List.of("COMPLETE_COURSE")).learnerRecordTypes(List.of("COURSE")).build(), 0, recordResponse);

        String expectedJson = """
                {
                  "userId": "userId",
                  "bookedCourses": [],
                  "learningPlanCourses": [
                    {
                      "id": "course4",
                      "title": "Course 4",
                      "shortDescription": "Course 4 short description",
                      "type": "blended",
                      "duration": 0,
                      "moduleCount": 2,
                      "costInPounds": 0,
                      "status": "IN_PROGRESS"
                    },
                    {
                      "id": "course7",
                      "title": "Course 7",
                      "shortDescription": "Course 7 short description",
                      "type": "elearning",
                      "duration": 0,
                      "moduleCount": 1,
                      "costInPounds": 0,
                      "status": "IN_PROGRESS"
                    }
                  ]
                }""";

        mockMvc.perform(get("/learning/plan?HOMEPAGE_COMPLETE_LEARNING_PLAN_COURSES=false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(expectedJson, true));
    }

    @Test
    public void testGetLearningPlanWhenWhenAllModulesAreCompletedAndHomepageCompleteLearningPlanCoursesIsTrue() throws Exception {

        String courses47 = ArrayJsonContentBuilder.create(course4, course7).build();

        String recordResponse = ArrayJsonContentBuilder.create(
                JsonLearnerRecordBuilder.create("userId", "course4").addLatestEvent("MOVE_TO_LEARNING_PLAN", "2023-01-01T10:00:00"),
                JsonLearnerRecordBuilder.create("userId", "course7")
        ).getAsPaginated(0, 20, 1).toString();

        String moduleRecordResponse = ArrayJsonContentBuilder.create(
                JsonModuleRecordBuilder.create("module2", "course4", "userId", "link", "2024-01-01T10:00:00")
                        .addCompletionDate("2025-01-01T10:00:00", "2025-01-01T10:00:00").addState("COMPLETED"),
                JsonModuleRecordBuilder.create("module3", "course4", "userId", "link", "2024-01-01T10:00:00")
                        .addCompletionDate("2025-01-01T10:00:00", "2025-01-01T10:00:00").addState("COMPLETED"),
                JsonModuleRecordBuilder.create("module6", "course7", "userId", "link", "2024-01-01T10:00:00")
                        .addUpdatedAt("2025-01-01T10:00:00").addState("IN_PROGRESS")
        ).getAsObjectList("moduleRecords").toString();

        String courseRecords = """
                {
                    "content": [
                        {
                            "resourceId": "course4",
                            "learnerId": "userId",
                            "recordType": {
                                "type": "COURSE"
                            }
                        }
                    ],
                    "totalPages": 1
                }
                """;
        String learnerRecords = """
                [{
                    "learnerId": "userId",
                    "resourceId": "course4",
                    "eventType": "COMPLETE_COURSE",
                    "eventTimestamp" : "2025-01-01T10:00:00",
                    "eventSource": "csl_source_id"
                }]
                """;
        String createLearnerRecordEventsResponse = """
                {
                    "successfulResources": [{
                        "learnerId": "userId",
                        "resourceId": "course4",
                        "eventType": {
                            "eventType": "COMPLETE_COURSE",
                            "learnerRecordType": {
                                "type": "COURSE"
                            }
                        },
                        "eventTimestamp" : "2025-01-01T10:00:00",
                        "eventSource": {"source": "csl_source_id"}
                    }],
                    "failedResources": []
                }
                """;
        String courseCompletionEmailMessage = """
                {
                    "recipient" : "lineManager@email.com",
                    "personalisation": {
                        "learnerEmailAddress" : "userEmail@email.com",
                        "learner" : "Learner",
                        "courseTitle" : "Course 4",
                        "manager": "Manager"
                    }
                }
                """;

        cslStubService.getLearningCatalogue().getMandatoryLearningMap(requiredLearningMap);
        cslStubService.getLearningCatalogue().getCourses(List.of("course4", "course7"), courses47);
        cslStubService.getCsrsStubService().getCivilServant("userId", testDataService.generateCivilServant());
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module2", "module3", "module6"), moduleRecordResponse);
        cslStubService.getLearnerRecord().getLearnerRecords(LearnerRecordQuery.builder().notEventTypes(List.of("COMPLETE_COURSE")).learnerRecordTypes(List.of("COURSE")).build(), 0, recordResponse);
        cslStubService.getLearnerRecord().getLearnerRecords("userId", "course4", 0, courseRecords);
        cslStubService.getLearnerRecord().createLearnerRecordEvent(learnerRecords, createLearnerRecordEventsResponse);
        cslStubService.getNotificationServiceStubService().sendEmail("NOTIFY_LINE_MANAGER_COMPLETED_LEARNING", courseCompletionEmailMessage);

        String expectedJson = """
                {
                  "userId": "userId",
                  "bookedCourses": [],
                  "learningPlanCourses": [
                    {
                      "id": "course7",
                      "title": "Course 7",
                      "shortDescription": "Course 7 short description",
                      "type": "elearning",
                      "duration": 0,
                      "moduleCount": 1,
                      "costInPounds": 0,
                      "status": "IN_PROGRESS"
                    }
                  ]
                }""";

        mockMvc.perform(get("/learning/plan?HOMEPAGE_COMPLETE_LEARNING_PLAN_COURSES=true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(expectedJson, true));
    }
}
