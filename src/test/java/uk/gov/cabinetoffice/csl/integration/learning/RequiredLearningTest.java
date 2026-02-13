package uk.gov.cabinetoffice.csl.integration.learning;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEventQuery;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RequiredLearningTest extends IntegrationTestBase {

    @Autowired
    private CSLStubService cslStubService;

    @Autowired
    private TestDataService testDataService;

    private CivilServant civilServant;

    @PostConstruct
    public void postConstruct() {
        civilServant = testDataService.generateCivilServant();
    }

    String depToCourseRequiredLearningCourseSingleModule = """
            {
                "departmentCodeMap": {
                    "CO": ["course1"]
                }
            }
            """;

    String courseSingleModule = """
            [{
                  "id": "course1",
                  "title": "Course 1",
                  "shortDescription": "Course 1",
                  "description": "Course 1",
                  "modules": [
                          {
                                  "type": "link",
                                  "url": "https://www.gov.uk/",
                                  "id": "module1",
                                  "title": "module1",
                                  "description": "module1",
                                  "optional": false,
                                  "moduleType": "link"
                          }
                  ],
                  "audiences": [
                          {
                                  "id": "CO",
                                  "name": "Cabinet Office",
                                  "areasOfWork": [],
                                  "departments": ["CO"],
                                  "grades": [],
                                  "interests": [],
                                  "requiredBy": "2024-01-01T00:00:00Z",
                                  "frequency": "P1Y",
                                  "type": "REQUIRED_LEARNING",
                                  "eventId": null
                          }
                  ],
                  "visibility": "PUBLIC",
                  "status": "Published",
                  "cost": 0.0
              }]""";

    String depToCourseRequiredLearningCourseMultipleModules = """
            {
                "departmentCodeMap": {
                    "HMRC": ["course1"],
                    "DWP": ["course1"]
                }
            }
            """;

    private String courseMultipleModules = """
            [{
                "id": "course1",
                "title": "Course 1",
                "shortDescription": "Short description of course 1",
                "description": "Course 1",
                "modules": [
                        {
                                "type": "link",
                                "url": "https://www.gov.uk/",
                                "id": "module1",
                                "title": "module1",
                                "duration": 600,
                                "description": "module1",
                                "optional": false,
                                "moduleType": "link"
                        },
                        {
                                "type": "elearning",
                                "url": "https://www.gov.uk/",
                                "id": "module2",
                                "title": "module2",
                                "duration": 1200,
                                "description": "module2",
                                "optional": false,
                                "moduleType": "elearning"
                        },
                        {
                                "type": "link",
                                "url": "https://www.gov.uk/",
                                "id": "module3",
                                "title": "module3",
                                "duration": 1800,
                                "description": "module3",
                                "optional": true,
                                "moduleType": "link"
                        }
                ],
                "audiences": [
                        {
                                "id": "HMRC",
                                "name": "HMRC",
                                "areasOfWork": [],
                                "departments": ["HMRC"],
                                "grades": [],
                                "interests": [],
                                "requiredBy": "2024-07-01T00:00:00Z",
                                "frequency": "P1Y",
                                "type": "REQUIRED_LEARNING",
                                "eventId": null
                        },
                        {
                                "id": "DWP",
                                "name": "DWP",
                                "areasOfWork": [],
                                "departments": ["DWP"],
                                "grades": [],
                                "interests": [],
                                "requiredBy": "2024-06-01T00:00:00Z",
                                "frequency": "P1Y",
                                "type": "REQUIRED_LEARNING",
                                "eventId": null
                        }
                ],
                "visibility": "PUBLIC",
                "status": "Published",
                "cost": 0.0
            }]""";

    String learnerRecordEventsResponseWithCourseWithOneContentResult = """
                {
                    "content": [
                        {
                            "eventTimestamp": "2025-12-09T10:00:00Z",
                            "resourceId": "course1"
                        }
                    ],
                    "totalPages": 1
                }
                """;

    String learnerRecordEventsResponseWithNoContent = """
                {
                    "content": [],
                    "totalPages": 1
                }
                """;

    @Test
    public void testGetRequiredLearningForUserNotStarted() throws Exception {
        String courseRecord = """
                {
                    "content": [],
                    "totalPages": 1
                }
                """;
        String moduleRecords = """
                {
                    "moduleRecords": []
                }
                """;
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(depToCourseRequiredLearningCourseSingleModule);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courseSingleModule);
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1"), moduleRecords);
        cslStubService.getLearnerRecord().getLearnerRecords("userId", "course1", 0, courseRecord);
        cslStubService.getLearnerRecord().getLearnerRecordEvents(0,
                LearnerRecordEventQuery.builder().userId("userId")
                        .eventTypes(List.of("COMPLETE_COURSE")).build(), learnerRecordEventsResponseWithNoContent);

        mockMvc.perform(get("/learning/required/detailed/userId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].courseId").value("course1"))
                .andExpect(jsonPath("$.courses[0].courseTitle").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].shortDescription").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].status").value("NULL"))
                .andExpect(jsonPath("$.courses[0].requiredModules").value(1))
                .andExpect(jsonPath("$.courses[0].completedRequiredModules").value(0))
                .andExpect(jsonPath("$.courses[0].modules.length()").value(1))
                .andExpect(jsonPath("$.courses[0].modules[0].moduleTitle").value("module1"))
                .andExpect(jsonPath("$.courses[0].modules[0].description").value("module1"))
                .andExpect(jsonPath("$.courses[0].modules[0].required").value(true))
                .andExpect(jsonPath("$.courses[0].modules[0].type").value("link"))
                .andExpect(jsonPath("$.courses[0].modules[0].status").value("NULL"));
    }

    @Test
    public void testGetRequiredLearningForUserInProgress() throws Exception {
        String courseRecord = """
                {
                    "content": [
                        {
                            "resourceId": "course1",
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
                            "courseId": "course1",
                            "moduleId": "module1",
                            "moduleTitle": "module1",
                            "moduleType": "link",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2024-07-02T10:00:00",
                            "createdAt": "2024-07-02T10:00:00",
                            "updatedAt": "2024-07-02T10:00:00"
                        },
                        {
                            "id": 2,
                            "uid": "module2",
                            "userId": "userId",
                            "courseId": "course1",
                            "moduleId": "module2",
                            "moduleTitle": "module2",
                            "moduleType": "elearning",
                            "duration": 3600,
                            "state": "IN_PROGRESS",
                            "completionDate": null,
                            "createdAt": "2023-07-02T10:00:00",
                            "updatedAt": "2023-07-02T10:00:00"
                        }
                    ]
                }
                """;
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(depToCourseRequiredLearningCourseMultipleModules);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courseMultipleModules);
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1", "module2", "module3"), moduleRecords);
        cslStubService.getLearnerRecord().getLearnerRecords("userId", "course1", 0, courseRecord);
        cslStubService.getLearnerRecord().getLearnerRecordEvents(0,
                LearnerRecordEventQuery.builder().userId("userId")
                        .eventTypes(List.of("COMPLETE_COURSE")).build(), learnerRecordEventsResponseWithNoContent);

        mockMvc.perform(get("/learning/required/detailed/userId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].courseId").value("course1"))
                .andExpect(jsonPath("$.courses[0].courseTitle").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].shortDescription").value("Short description of course 1"))
                .andExpect(jsonPath("$.courses[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.courses[0].requiredModules").value(2))
                .andExpect(jsonPath("$.courses[0].completedRequiredModules").value(1))
                .andExpect(jsonPath("$.courses[0].audience.name").value("DWP"))
                .andExpect(jsonPath("$.courses[0].audience.frequency").value("1 years, 0 months"))
                .andExpect(jsonPath("$.courses[0].audience.learningPeriod.startDate").value("2022-06-01"))
                .andExpect(jsonPath("$.courses[0].audience.learningPeriod.endDate").value("2023-06-01"))
                .andExpect(jsonPath("$.courses[0].modules.length()").value(3))
                .andExpect(jsonPath("$.courses[0].modules[0].moduleTitle").value("module1"))
                .andExpect(jsonPath("$.courses[0].modules[0].description").value("module1"))
                .andExpect(jsonPath("$.courses[0].modules[0].required").value(true))
                .andExpect(jsonPath("$.courses[0].modules[0].type").value("link"))
                .andExpect(jsonPath("$.courses[0].modules[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$.courses[0].modules[1].moduleTitle").value("module2"))
                .andExpect(jsonPath("$.courses[0].modules[1].description").value("module2"))
                .andExpect(jsonPath("$.courses[0].modules[1].required").value(true))
                .andExpect(jsonPath("$.courses[0].modules[1].type").value("elearning"))
                .andExpect(jsonPath("$.courses[0].modules[1].status").value("IN_PROGRESS"));
    }

    @Test
    public void testGetRequiredLearningForUserSingleModuleInProgress() throws Exception {
        String courseRecord = """
                {
                    "content": [
                        {
                            "resourceId": "course1",
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
                            "courseId": "course1",
                            "moduleId": "module1",
                            "moduleTitle": "module1",
                            "moduleType": "link",
                            "duration": 3600,
                            "state": "NULL",
                            "completionDate": null,
                            "createdAt": null,
                            "updatedAt": null
                        },
                        {
                            "id": 2,
                            "uid": "module2",
                            "userId": "userId",
                            "courseId": "course1",
                            "moduleId": "module2",
                            "moduleTitle": "module2",
                            "moduleType": "elearning",
                            "duration": 3600,
                            "state": "IN_PROGRESS",
                            "completionDate": null,
                            "createdAt": "2023-07-02T10:00:00",
                            "updatedAt": "2023-07-02T10:00:00"
                        }
                    ]
                }
                """;
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(depToCourseRequiredLearningCourseMultipleModules);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courseMultipleModules);
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1", "module2", "module3"), moduleRecords);
        cslStubService.getLearnerRecord().getLearnerRecords("userId", "course1", 0, courseRecord);
        cslStubService.getLearnerRecord().getLearnerRecordEvents(0,
                LearnerRecordEventQuery.builder().userId("userId")
                        .eventTypes(List.of("COMPLETE_COURSE")).build(), learnerRecordEventsResponseWithNoContent);

        mockMvc.perform(get("/learning/required/detailed/userId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].courseId").value("course1"))
                .andExpect(jsonPath("$.courses[0].courseTitle").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].shortDescription").value("Short description of course 1"))
                .andExpect(jsonPath("$.courses[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.courses[0].requiredModules").value(2))
                .andExpect(jsonPath("$.courses[0].completedRequiredModules").value(0))
                .andExpect(jsonPath("$.courses[0].audience.name").value("DWP"))
                .andExpect(jsonPath("$.courses[0].audience.frequency").value("1 years, 0 months"))
                .andExpect(jsonPath("$.courses[0].audience.learningPeriod.startDate").value("2022-06-01"))
                .andExpect(jsonPath("$.courses[0].audience.learningPeriod.endDate").value("2023-06-01"))
                .andExpect(jsonPath("$.courses[0].modules.length()").value(3))
                .andExpect(jsonPath("$.courses[0].modules[0].moduleTitle").value("module1"))
                .andExpect(jsonPath("$.courses[0].modules[0].description").value("module1"))
                .andExpect(jsonPath("$.courses[0].modules[0].required").value(true))
                .andExpect(jsonPath("$.courses[0].modules[0].type").value("link"))
                .andExpect(jsonPath("$.courses[0].modules[0].status").value("NULL"))
                .andExpect(jsonPath("$.courses[0].modules[1].moduleTitle").value("module2"))
                .andExpect(jsonPath("$.courses[0].modules[1].description").value("module2"))
                .andExpect(jsonPath("$.courses[0].modules[1].required").value(true))
                .andExpect(jsonPath("$.courses[0].modules[1].type").value("elearning"))
                .andExpect(jsonPath("$.courses[0].modules[1].status").value("IN_PROGRESS"));
    }

    @Test
    public void testGetRequiredLearningForUserCompletedLastLearningPeriod() throws Exception {
        String courseRecord = """
                {
                    "content": [
                        {
                            "resourceId": "course1",
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
                            "courseId": "course1",
                            "moduleId": "module1",
                            "moduleTitle": "module1",
                            "moduleType": "link",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2022-01-01T10:00:00",
                            "createdAt": "2022-01-01T10:00:00",
                            "updatedAt": "2022-01-01T10:00:00"
                        },
                        {
                            "id": 2,
                            "uid": "module2",
                            "userId": "userId",
                            "courseId": "course1",
                            "moduleId": "module2",
                            "moduleTitle": "module2",
                            "moduleType": "elearning",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2022-01-01T10:00:00",
                            "createdAt": "2022-01-01T10:00:00",
                            "updatedAt": "2022-01-01T10:00:00"
                        }
                    ]
                }
                """;
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(depToCourseRequiredLearningCourseMultipleModules);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courseMultipleModules);
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1", "module2", "module3"), moduleRecords);
        cslStubService.getLearnerRecord().getLearnerRecords("userId", "course1", 0, courseRecord);
        cslStubService.getLearnerRecord().getLearnerRecordEvents(0,
                LearnerRecordEventQuery.builder().userId("userId")
                        .eventTypes(List.of("COMPLETE_COURSE")).build(), learnerRecordEventsResponseWithNoContent);
        mockMvc.perform(get("/learning/required/detailed/userId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].courseId").value("course1"))
                .andExpect(jsonPath("$.courses[0].courseTitle").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].shortDescription").value("Short description of course 1"))
                .andExpect(jsonPath("$.courses[0].status").value("NULL"))
                .andExpect(jsonPath("$.courses[0].requiredModules").value(2))
                .andExpect(jsonPath("$.courses[0].completedRequiredModules").value(0))
                .andExpect(jsonPath("$.courses[0].audience.name").value("DWP"))
                .andExpect(jsonPath("$.courses[0].audience.frequency").value("1 years, 0 months"))
                .andExpect(jsonPath("$.courses[0].audience.learningPeriod.startDate").value("2022-06-01"))
                .andExpect(jsonPath("$.courses[0].audience.learningPeriod.endDate").value("2023-06-01"))
                .andExpect(jsonPath("$.courses[0].modules.length()").value(3))
                .andExpect(jsonPath("$.courses[0].modules[0].moduleTitle").value("module1"))
                .andExpect(jsonPath("$.courses[0].modules[0].description").value("module1"))
                .andExpect(jsonPath("$.courses[0].modules[0].required").value(true))
                .andExpect(jsonPath("$.courses[0].modules[0].type").value("link"))
                .andExpect(jsonPath("$.courses[0].modules[0].status").value("NULL"))
                .andExpect(jsonPath("$.courses[0].modules[1].moduleTitle").value("module2"))
                .andExpect(jsonPath("$.courses[0].modules[1].description").value("module2"))
                .andExpect(jsonPath("$.courses[0].modules[1].required").value(true))
                .andExpect(jsonPath("$.courses[0].modules[1].type").value("elearning"))
                .andExpect(jsonPath("$.courses[0].modules[1].status").value("NULL"));
    }

    @Test
    public void testGetRequiredLearningBasicCompleted() throws Exception {
        String eventsResponse = """
                {
                    "content": [
                        {
                            "eventTimestamp": "2025-01-01T00:00:00Z",
                            "resourceId": "course1"
                        }
                    ],
                    "totalPages": 1
                }
                """;
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(depToCourseRequiredLearningCourseSingleModule);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courseSingleModule);
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearnerRecord().getLearnerRecordEvents(0,
                LearnerRecordEventQuery.builder().userId("userId")
                        .resourceIds(List.of("course1"))
                        .eventTypes(List.of("COMPLETE_COURSE")).build(), eventsResponse);
        mockMvc.perform(get("/learning/required")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.userId").value("userId"))
                .andExpect(jsonPath("$.courses.length()").value(0));
    }

    @Test
    public void testGetRequiredLearningCompletedButShowingInProgressStatusDueToMissingCompletionEvent() throws Exception {
        String eventsResponse = """
                {
                    "content": [
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
                            "courseId": "course1",
                            "moduleId": "module1",
                            "moduleTitle": "module1",
                            "moduleType": "link",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2025-01-01T10:00:00",
                            "createdAt": "2025-01-01T10:00:00",
                            "updatedAt": "2025-01-01T10:00:00"
                        },
                        {
                            "id": 2,
                            "uid": "module2",
                            "userId": "userId",
                            "courseId": "course1",
                            "moduleId": "module2",
                            "moduleTitle": "module2",
                            "moduleType": "elearning",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2022-06-01T10:00:00",
                            "createdAt": "2025-01-01T10:00:00",
                            "updatedAt": "2025-01-01T10:00:00"
                        }
                    ]
                }
                """;
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(depToCourseRequiredLearningCourseMultipleModules);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courseMultipleModules);
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearnerRecord().getLearnerRecordEvents(0,
                LearnerRecordEventQuery.builder().userId("userId")
                        .resourceIds(List.of("course1"))
                        .eventTypes(List.of("COMPLETE_COURSE")).build(), eventsResponse);
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1", "module2"), moduleRecords);
        mockMvc.perform(get("/learning/required?HOMEPAGE_COMPLETE_REQUIRED_COURSES=false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.userId").value("userId"))
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].id").value("course1"))
                .andExpect(jsonPath("$.courses[0].title").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].shortDescription").value("Short description of course 1"))
                .andExpect(jsonPath("$.courses[0].type").value("blended"))
                .andExpect(jsonPath("$.courses[0].duration").value(3600))
                .andExpect(jsonPath("$.courses[0].moduleCount").value(3))
                .andExpect(jsonPath("$.courses[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.courses[0].dueBy").value("2023-06-01"));
    }

    @Test
    public void testGetRequiredLearningUpdateCompletedStatusForMissingCompletionEvent() throws Exception {
        String learnerRecordEvents = """
                {
                    "content": [],
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
                            "courseId": "course1",
                            "moduleId": "module1",
                            "moduleTitle": "module1",
                            "moduleType": "link",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2025-01-01T10:00:00",
                            "createdAt": "2025-01-01T10:00:00",
                            "updatedAt": "2025-01-01T10:00:00"
                        },
                        {
                            "id": 2,
                            "uid": "module2",
                            "userId": "userId",
                            "courseId": "course1",
                            "moduleId": "module2",
                            "moduleTitle": "module2",
                            "moduleType": "elearning",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2022-06-01T10:00:00",
                            "createdAt": "2025-01-01T10:00:00",
                            "updatedAt": "2025-01-01T10:00:00"
                        }
                    ]
                }
                """;
        String courseRecords = """
                {
                    "content": [
                        {
                            "resourceId": "course1",
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
                    "resourceId": "course1",
                    "eventType": "COMPLETE_COURSE",
                    "eventTimestamp" : "2025-01-01T10:00:00",
                    "eventSource": "csl_source_id"
                }]
                """;
        String createLearnerRecordEventsResponse = """
                {
                    "successfulResources": [{
                        "learnerId": "userId",
                        "resourceId": "course1",
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
                        "courseTitle" : "Course 1",
                        "manager": "Manager"
                    }
                }
                """;

        cslStubService.getLearningCatalogue().getMandatoryLearningMap(depToCourseRequiredLearningCourseMultipleModules);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courseMultipleModules);
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearnerRecord().getLearnerRecordEvents(0,
                LearnerRecordEventQuery.builder().userId("userId")
                        .resourceIds(List.of("course1"))
                        .eventTypes(List.of("COMPLETE_COURSE")).build(), learnerRecordEvents);
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1", "module2"), moduleRecords);
        cslStubService.getLearnerRecord().getLearnerRecords("userId", "course1", 0, courseRecords);
        cslStubService.getLearnerRecord().createLearnerRecordEvent(learnerRecords, createLearnerRecordEventsResponse);
        cslStubService.getNotificationServiceStubService().sendEmail("NOTIFY_LINE_MANAGER_COMPLETED_LEARNING", courseCompletionEmailMessage);
        mockMvc.perform(get("/learning/required?HOMEPAGE_COMPLETE_REQUIRED_COURSES=true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.userId").value("userId"))
                .andExpect(jsonPath("$.courses.length()").value(0));
    }

    @Test
    public void testGetRequiredLearningBasicNotCompleted() throws Exception {
        String eventsResponse = """
                {
                    "content": [
                        {
                            "eventTimestamp": "2022-01-01T10:00:00Z",
                            "resourceId": "course1"
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
                            "courseId": "course1",
                            "moduleId": "module1",
                            "moduleTitle": "module1",
                            "moduleType": "link",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2022-01-01T10:00:00Z",
                            "createdAt": "2022-01-01T10:00:00Z",
                            "updatedAt": "2022-01-01T10:00:00Z"
                        },
                        {
                            "id": 2,
                            "uid": "module2",
                            "userId": "userId",
                            "courseId": "course1",
                            "moduleId": "module2",
                            "moduleTitle": "module2",
                            "moduleType": "elearning",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2022-01-01T10:00:00Z",
                            "createdAt": "2022-01-01T10:00:00Z",
                            "updatedAt": "2022-01-01T10:00:00Z"
                        }
                    ]
                }
                """;
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(depToCourseRequiredLearningCourseMultipleModules);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courseMultipleModules);
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearnerRecord().getLearnerRecordEvents(0,
                LearnerRecordEventQuery.builder().userId("userId")
                        .resourceIds(List.of("course1"))
                        .eventTypes(List.of("COMPLETE_COURSE")).build(), eventsResponse);
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1", "module2"), moduleRecords);
        mockMvc.perform(get("/learning/required")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.userId").value("userId"))
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].id").value("course1"))
                .andExpect(jsonPath("$.courses[0].title").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].shortDescription").value("Short description of course 1"))
                .andExpect(jsonPath("$.courses[0].type").value("blended"))
                .andExpect(jsonPath("$.courses[0].duration").value(3600))
                .andExpect(jsonPath("$.courses[0].moduleCount").value(3))
                .andExpect(jsonPath("$.courses[0].status").value("NULL"))
                .andExpect(jsonPath("$.courses[0].dueBy").value("2023-06-01"));
    }

    @Test
    public void testRequiredLearningEndpointReturnsCourse1AsInProgressIfCompletedOnPreviousLearningPeriod() throws Exception {
        String eventsResponse = """
                {
                    "content": [
                        {
                            "eventTimestamp": "2022-01-01T10:00:00Z",
                            "resourceId": "course1"
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
                            "courseId": "course1",
                            "moduleId": "module1",
                            "moduleTitle": "module1",
                            "moduleType": "link",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2022-06-02T10:00:00Z",
                            "createdAt": "2022-06-02T10:00:00Z",
                            "updatedAt": "2022-06-02T10:00:00Z"
                        },
                        {
                            "id": 2,
                            "uid": "module2",
                            "userId": "userId",
                            "courseId": "course1",
                            "moduleId": "module2",
                            "moduleTitle": "module2",
                            "moduleType": "elearning",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2022-06-02T10:00:00Z",
                            "createdAt": "2022-06-02T10:00:00Z",
                            "updatedAt": "2022-06-02T10:00:00Z"
                        }
                    ]
                }
                """;
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(depToCourseRequiredLearningCourseMultipleModules);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courseMultipleModules);
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearnerRecord().getLearnerRecordEvents(0,
                LearnerRecordEventQuery.builder().userId("userId")
                        .resourceIds(List.of("course1"))
                        .eventTypes(List.of("COMPLETE_COURSE")).build(), eventsResponse);
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1", "module2"), moduleRecords);
        mockMvc.perform(get("/learning/required")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.userId").value("userId"))
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].id").value("course1"))
                .andExpect(jsonPath("$.courses[0].title").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].shortDescription").value("Short description of course 1"))
                .andExpect(jsonPath("$.courses[0].type").value("blended"))
                .andExpect(jsonPath("$.courses[0].duration").value(3600))
                .andExpect(jsonPath("$.courses[0].moduleCount").value(3))
                .andExpect(jsonPath("$.courses[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.courses[0].dueBy").value("2023-06-01"));
    }

    @Test
    public void testGetRequiredLearningMapForOrganisations() throws Exception {
        Course course1 = testDataService.generateCourse(false, false);
        course1.setTitle("course 1");
        course1.setId("course1");
        Course course2 = testDataService.generateCourse(false, false);
        course2.setTitle("course 2");
        course2.setId("course2");
        Course course3 = testDataService.generateCourse(false, false);
        course3.setTitle("course 3");
        course3.setId("course3");
        cslStubService.getCsrsStubService().getOrganisations(testDataService.generateOrganisationalUnitsPagedResponse());
        cslStubService.getLearningCatalogue().getMandatoryLearningMap("""
                {
                    "departmentCodeMap": {
                        "ON1": ["course1", "course2"],
                        "ON5": ["course3"]
                    }
                }
                """);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1", "course2", "course3"),
                List.of(course1, course2, course3));
        mockMvc.perform(get("/learning/required/for-departments")
                        .param("organisationIds", "1")
                        .param("organisationIds", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.departmentMap.1[0].id").value("course1"))
                .andExpect(jsonPath("$.departmentMap.1[0].title").value("course 1"))
                .andExpect(jsonPath("$.departmentMap.1[1].id").value("course2"))
                .andExpect(jsonPath("$.departmentMap.1[1].title").value("course 2"))
                .andExpect(jsonPath("$.departmentMap.5[0].id").value("course3"))
                .andExpect(jsonPath("$.departmentMap.5[0].title").value("course 3"))
                .andExpect(jsonPath("$.departmentMap.5[1].id").value("course1"))
                .andExpect(jsonPath("$.departmentMap.5[1].title").value("course 1"))
                .andExpect(jsonPath("$.departmentMap.5[2].id").value("course2"))
                .andExpect(jsonPath("$.departmentMap.5[2].title").value("course 2"));
    }

}
