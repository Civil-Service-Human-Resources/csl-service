package uk.gov.cabinetoffice.csl.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

@Slf4j
public class CourseOverviewTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CSLStubService cslStubService;

    private JsonCourseBuilder jsonCourse;

    @BeforeEach
    public void before() {
        jsonCourse = JsonCourseBuilder.create("course1", "A Course 1")
                .addModule("link", "module1", "module 1", false, 0)
                .addModule("elearning", "module2", "module 2", true, 100)
                .addFileModule("module3", "module 3", false, 200)
                .addLearningTag(1L, "Project Management", "project-management")
                .addAudience(new String[]{"AA", "AO", "G7"}, new String[]{"Commercial", "Finance"});
    }

    @Test
    public void testCourseOverviewForBlendedCourse() throws Exception {
        String course = ArrayJsonContentBuilder.create(jsonCourse).get().toString();
        String moduleRecordResponse = ArrayJsonContentBuilder.create(
                JsonModuleRecordBuilder.create("module1", "course1", "userId", "link", "2025-01-01T09:00:00")
                        .addUpdatedAt("2025-01-01T09:00:00").addState("IN_PROGRESS"),
                JsonModuleRecordBuilder.create("module2", "course1", "userId", "link", "2022-01-02T09:00:00")
                        .addUpdatedAt("2022-01-02T09:00:00").addState("COMPLETED")
        ).getAsObjectList("moduleRecords").toString();
        String learnerRecordResponse = ArrayJsonContentBuilder.create(JsonLearnerRecordBuilder.create("userId", "course1")
                .addLatestEvent("MOVE_TO_LEARNING_PLAN", "2022-01-02T09:00:00")).getAsPaginatedAndBuild(0, 1, 1);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), course);
        cslStubService.getCsrsStubService().getCivilServant("userId", testDataService.generateCivilServant());
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1", "module2", "module3"), moduleRecordResponse);
        cslStubService.getLearnerRecord().getLearnerRecords("userId", "course1", 0, learnerRecordResponse);

        mockMvc.perform(get("/courses/course1/overview")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json("""
                        {
                            "id": "course1",
                            "title": "A Course 1",
                            "description": "A Course 1",
                            "learningOutcomes": null,
                            "status": "PUBLISHED",
                            "isInLearningPlan": "IS_IN_LEARNING_PLAN",
                            "type": "blended",
                            "duration": 300,
                            "learningTags": [
                                {
                                    "id": 1,
                                    "urlSlug": "project-management",
                                    "name": "Project Management"
                                }
                            ],
                            "grades": [
                                "AA",
                                "G7",
                                "AO"
                            ],
                            "areasOfWork": [
                                "Commercial",
                                "Finance"
                            ],
                            "costInPounds": 0,
                            "modules": [
                                {
                                    "id": "module1",
                                    "title": "module 1",
                                    "description": "module 1",
                                    "mandatory": true,
                                    "associatedLearning": false,
                                    "type": "link",
                                    "duration": 0,
                                    "costInPounds": 0,
                                    "state": "IN_PROGRESS",
                                    "mustConfirmBooking": false
                                },
                                {
                                    "id": "module2",
                                    "title": "module 2",
                                    "description": "module 2",
                                    "mandatory": false,
                                    "associatedLearning": false,
                                    "type": "elearning",
                                    "duration": 100,
                                    "costInPounds": 0,
                                    "state": "COMPLETED",
                                    "mustConfirmBooking": false
                                },
                                {
                                    "id": "module3",
                                    "title": "module 3",
                                    "description": "module 3",
                                    "mandatory": true,
                                    "associatedLearning": false,
                                    "type": "file",
                                    "duration": 200,
                                    "costInPounds": 0,
                                    "state": "NULL",
                                    "mustConfirmBooking": false,
                                    "filename": "file",
                                    "extension": "txt",
                                    "sizeInKb": 1000
                                }
                            ],
                            "mandatoryModuleCount": 2
                        }
                        """, true));
    }

    @Test
    public void testCourseOverviewForRequiredCourse() throws Exception {
        cslStubService.getLearningCatalogue().getMandatoryLearningMap("""
                {
                    "departmentCodeMap": {
                        "CO": ["course1"]
                    }
                }
                """);
        String course = ArrayJsonContentBuilder.create(jsonCourse
                .addDepartmentRequiredLearning("CO", "2024-01-01T00:00:00Z", "P1Y")).get().toString();
        String moduleRecordResponse = ArrayJsonContentBuilder.create(
                JsonModuleRecordBuilder.create("module1", "course1", "userId", "link", "2025-01-01T09:00:00")
                        .addUpdatedAt("2025-01-01T09:00:00").addState("COMPLETED"),
                JsonModuleRecordBuilder.create("module2", "course1", "userId", "link", "2021-01-02T09:00:00")
                        .addUpdatedAt("2021-01-02T09:00:00").addState("IN_PROGRESS")
        ).getAsObjectList("moduleRecords").toString();
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), course);
        cslStubService.getCsrsStubService().getCivilServant("userId", testDataService.generateCivilServant());
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1", "module2", "module3"), moduleRecordResponse);

        mockMvc.perform(get("/courses/course1/overview")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json("""
                        {
                            "id": "course1",
                            "title": "A Course 1",
                            "description": "A Course 1",
                            "learningOutcomes": null,
                            "status": "PUBLISHED",
                            "isInLearningPlan": "CANNOT_BE_ADDED_TO_LEARNING_PLAN",
                            "type": "blended",
                            "duration": 300,
                            "learningTags": [
                                {
                                    "id": 1,
                                    "urlSlug": "project-management",
                                    "name": "Project Management"
                                }
                            ],
                            "grades": [
                                "AA",
                                "G7",
                                "AO"
                            ],
                            "areasOfWork": [
                                "Commercial",
                                "Finance"
                            ],
                            "costInPounds": 0,
                            "modules": [
                                {
                                    "id": "module1",
                                    "title": "module 1",
                                    "description": "module 1",
                                    "mandatory": true,
                                    "associatedLearning": false,
                                    "type": "link",
                                    "duration": 0,
                                    "costInPounds": 0,
                                    "state": "IN_PROGRESS",
                                    "mustConfirmBooking": false
                                },
                                {
                                    "id": "module2",
                                    "title": "module 2",
                                    "description": "module 2",
                                    "mandatory": false,
                                    "associatedLearning": false,
                                    "type": "elearning",
                                    "duration": 100,
                                    "costInPounds": 0,
                                    "state": "NULL",
                                    "mustConfirmBooking": false
                                },
                                {
                                    "id": "module3",
                                    "title": "module 3",
                                    "description": "module 3",
                                    "mandatory": true,
                                    "associatedLearning": false,
                                    "type": "file",
                                    "duration": 200,
                                    "costInPounds": 0,
                                    "state": "NULL",
                                    "mustConfirmBooking": false,
                                    "filename": "file",
                                    "extension": "txt",
                                    "sizeInKb": 1000
                                }
                            ],
                            "mandatoryModuleCount": 2
                        }
                        """, true));
    }

    @Test
    public void testCourseOverviewForBookedCourse() throws Exception {
        String course = ArrayJsonContentBuilder.create(JsonCourseBuilder.create("course1", "A Course 1")
                .addFaceToFaceModule("module1", "Module 1", false, 100, "event1", BigDecimal.valueOf(100L),
                        new DateRangeJsonValues("12:00", "14:00", "2022-01-02"),
                        new DateRangeJsonValues("09:00", "11:00", "2022-01-01"))
                .addLearningTag(1L, "Project Management", "project-management")
                .addAudience(new String[]{"AA", "AO", "G7"}, new String[]{})).get().toString();
        String moduleRecordResponse = ArrayJsonContentBuilder.create(
                JsonModuleRecordBuilder.create("module1", "course1", "userId", "face-to-face", "2022-01-02T09:00:00")
                        .addUpdatedAt("2022-01-02T09:00:00").addState("APPROVED").addEvent("event1", "2022-01-02T09:00:00")
        ).getAsObjectList("moduleRecords").toString();
        String learnerRecordResponse = ArrayJsonContentBuilder.create(JsonLearnerRecordBuilder.create("userId", "course1")
                .addLatestEvent("MOVE_TO_LEARNING_PLAN", "2022-01-02T09:00:00")).getAsPaginatedAndBuild(0, 1, 1);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), course);
        cslStubService.getCsrsStubService().getCivilServant("userId", testDataService.generateCivilServant());
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1"), moduleRecordResponse);
        cslStubService.getLearnerRecord().getLearnerRecords("userId", "course1", 0, learnerRecordResponse);
        mockMvc.perform(get("/courses/course1/overview")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json("""
                        {
                            "id": "course1",
                            "title": "A Course 1",
                            "description": "A Course 1",
                            "learningOutcomes": null,
                            "status": "PUBLISHED",
                            "isInLearningPlan": "CANNOT_BE_ADDED_TO_LEARNING_PLAN",
                            "type": "face-to-face",
                            "duration": 14400,
                            "learningTags": [
                                {
                                    "id": 1,
                                    "urlSlug": "project-management",
                                    "name": "Project Management"
                                }
                            ],
                            "grades": [
                                "AA",
                                "G7",
                                "AO"
                            ],
                            "areasOfWork": [],
                            "costInPounds": 100,
                            "modules": [
                                {
                                    "id": "module1",
                                    "title": "Module 1",
                                    "description": "Module 1",
                                    "mandatory": true,
                                    "associatedLearning": false,
                                    "type": "face-to-face",
                                    "duration": 14400,
                                    "costInPounds": 100,
                                    "state": "APPROVED",
                                    "mustConfirmBooking": false,
                                    "eventId": "event1",
                                    "canBeBooked": false,
                                    "canBeCancelled": true
                                }
                            ],
                            "mandatoryModuleCount": 1
                        }
                        """, true));
    }

    @Test
    public void testCourseOverviewForArchivedCourse() throws Exception {
        String course = ArrayJsonContentBuilder.create(jsonCourse.status("ARCHIVED")).build();
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), course);
        mockMvc.perform(get("/courses/course1/overview")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json("""
                        {
                            "id": "course1",
                            "title": "A Course 1",
                            "description": "A Course 1",
                            "learningOutcomes": null,
                            "status": "ARCHIVED",
                            "isInLearningPlan": "CANNOT_BE_ADDED_TO_LEARNING_PLAN",
                            "type": "blended",
                            "duration": 300,
                            "learningTags": [
                                {
                                    "id": 1,
                                    "urlSlug": "project-management",
                                    "name": "Project Management"
                                }
                            ],
                            "grades": [
                                "AA",
                                "G7",
                                "AO"
                            ],
                            "areasOfWork": [
                                "Commercial",
                                "Finance"
                            ],
                            "costInPounds": 0,
                            "modules": [],
                            "mandatoryModuleCount": 0
                        }
                        """, false));
    }

}
