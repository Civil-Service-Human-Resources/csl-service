package uk.gov.cabinetoffice.csl.integration.learning;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
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
                                  "id": "aud1",
                                  "name": "audience1",
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

    String depToCourseRequiredLearningCourseMingleModules = """
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
                        },
                        {
                                "type": "elearning",
                                "url": "https://www.gov.uk/",
                                "id": "module2",
                                "title": "module2",
                                "description": "module2",
                                "optional": false,
                                "moduleType": "elearning"
                        },
                        {
                                "type": "link",
                                "url": "https://www.gov.uk/",
                                "id": "module3",
                                "title": "module3",
                                "description": "module3",
                                "optional": true,
                                "moduleType": "link"
                        }
                ],
                "audiences": [
                        {
                                "id": "aud1",
                                "name": "audience1",
                                "areasOfWork": [],
                                "departments": ["DWP"],
                                "grades": [],
                                "interests": [],
                                "requiredBy": "2024-06-01T00:00:00Z",
                                "frequency": "P1Y",
                                "type": "REQUIRED_LEARNING",
                                "eventId": null
                        },
                        {
                                "id": "aud2",
                                "name": "audience2",
                                "areasOfWork": [],
                                "departments": ["HMRC"],
                                "grades": [],
                                "interests": [],
                                "requiredBy": "2024-07-01T00:00:00Z",
                                "frequency": "P1Y",
                                "type": "REQUIRED_LEARNING",
                                "eventId": null
                        }
                ],
                "visibility": "PUBLIC",
                "status": "Published",
                "cost": 0.0
            }]""";

    @Test
    public void testGetRequiredLearningForUserNotStarted() throws Exception {
        String courseRecord = """
                {
                    "courseRecords": []
                }
                """;
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(depToCourseRequiredLearningCourseSingleModule);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courseSingleModule);
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearnerRecord().getCourseRecord("course1", "userId", courseRecord);
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
                    "courseRecords": [
                            {
                                "courseId": "course1",
                                "userId": "userId",
                                "courseTitle": "Course 1",
                                "state": "IN_PROGRESS",
                                "lastUpdated": "2024-07-02T10:00:00",
                                "required": false,
                                "modules": [
                                    {
                                        "id": 1,
                                        "uid": "module1",
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
                    ]
                }
                """;
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(depToCourseRequiredLearningCourseMingleModules);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courseMultipleModules);
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearnerRecord().getCourseRecord("course1", "userId", courseRecord);
        mockMvc.perform(get("/learning/required/detailed/userId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].courseId").value("course1"))
                .andExpect(jsonPath("$.courses[0].courseTitle").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].shortDescription").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.courses[0].requiredModules").value(2))
                .andExpect(jsonPath("$.courses[0].completedRequiredModules").value(1))
                .andExpect(jsonPath("$.courses[0].audience.name").value("audience2"))
                .andExpect(jsonPath("$.courses[0].audience.frequency").value("1 years, 0 months"))
                .andExpect(jsonPath("$.courses[0].audience.learningPeriod.startDate").value("2023-07-01"))
                .andExpect(jsonPath("$.courses[0].audience.learningPeriod.endDate").value("2024-07-01"))
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
                    "courseRecords": [
                            {
                                "courseId": "course1",
                                "userId": "userId",
                                "courseTitle": "Course 1",
                                "state": "IN_PROGRESS",
                                "lastUpdated": "2024-07-02T10:00:00",
                                "required": false,
                                "modules": [
                                    {
                                        "id": 1,
                                        "uid": "module1",
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
                    ]
                }
                """;
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(depToCourseRequiredLearningCourseMingleModules);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courseMultipleModules);
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearnerRecord().getCourseRecord("course1", "userId", courseRecord);
        mockMvc.perform(get("/learning/required/detailed/userId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].courseId").value("course1"))
                .andExpect(jsonPath("$.courses[0].courseTitle").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].shortDescription").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.courses[0].requiredModules").value(2))
                .andExpect(jsonPath("$.courses[0].completedRequiredModules").value(0))
                .andExpect(jsonPath("$.courses[0].audience.name").value("audience2"))
                .andExpect(jsonPath("$.courses[0].audience.frequency").value("1 years, 0 months"))
                .andExpect(jsonPath("$.courses[0].audience.learningPeriod.startDate").value("2023-07-01"))
                .andExpect(jsonPath("$.courses[0].audience.learningPeriod.endDate").value("2024-07-01"))
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
                    "courseRecords": [
                            {
                                "courseId": "course1",
                                "userId": "userId",
                                "courseTitle": "Course 1",
                                "state": "IN_PROGRESS",
                                "lastUpdated": "2023-06-01T10:00:00",
                                "required": false,
                                "modules": [
                                    {
                                        "id": 1,
                                        "uid": "module1",
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
                    ]
                }
                """;
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(depToCourseRequiredLearningCourseMingleModules);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courseMultipleModules);
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearnerRecord().getCourseRecord("course1", "userId", courseRecord);
        mockMvc.perform(get("/learning/required/detailed/userId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].courseId").value("course1"))
                .andExpect(jsonPath("$.courses[0].courseTitle").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].shortDescription").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].status").value("NULL"))
                .andExpect(jsonPath("$.courses[0].requiredModules").value(2))
                .andExpect(jsonPath("$.courses[0].completedRequiredModules").value(0))
                .andExpect(jsonPath("$.courses[0].audience.name").value("audience2"))
                .andExpect(jsonPath("$.courses[0].audience.frequency").value("1 years, 0 months"))
                .andExpect(jsonPath("$.courses[0].audience.learningPeriod.startDate").value("2023-07-01"))
                .andExpect(jsonPath("$.courses[0].audience.learningPeriod.endDate").value("2024-07-01"))
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


}
