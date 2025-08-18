package uk.gov.cabinetoffice.csl.integration.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LearningPlanTest extends IntegrationTestBase {

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

    String courses = """
            [{
                              "id": "course3",
                              "title": "Course 3",
                              "shortDescription": "Course 3 short description",
                              "description": "Course 3 description",
                              "modules": [
                                      {
                                              "type": "link",
                                              "url": "https://www.gov.uk/",
                                              "id": "module1",
                                              "title": "Module 1",
                                              "description": "Module 1 description",
                                              "optional": false,
                                              "moduleType": "link",
                                              "duration": 30,
                                              "cost": 0.0
                                      }
                              ],
                              "audiences": [],
                              "visibility": "PUBLIC",
                              "status": "Published",
                              "cost": 0.0
                          },{
                              "id": "course4",
                              "title": "Course 4",
                              "shortDescription": "Course 4 short description",
                              "description": "Course 4 description",
                              "modules": [
                                      {
                                              "type": "link",
                                              "url": "https://www.gov.uk/",
                                              "id": "module2",
                                              "title": "Module 2",
                                              "description": "Module 2 description",
                                              "optional": false,
                                              "moduleType": "link",
                                              "cost": 0.0
                                      },
                                    {
                                              "type": "file",
                                              "url": "https://www.gov.uk/",
                                              "id": "module3",
                                              "title": "Module 3",
                                              "description": "Module 3 description",
                                              "optional": false,
                                              "moduleType": "file",
                                              "cost": 0.0
                                      }
                              ],
                              "audiences": [],
                              "visibility": "PUBLIC",
                              "status": "Published",
                              "cost": 0.0
                          },{
                            "id": "course5",
                            "title": "Course 5",
                            "shortDescription": "Course 5 short description",
                            "description": "Course 5 description",
                            "modules": [
                                {
                                  "type": "face-to-face",
                                  "id": "module4",
                                  "title": "Module 4",
                                  "description": "Module 4 description",
                                  "optional": false,
                                  "moduleType": "face-to-face",
                                  "cost": 0.0,
                                  "events": [
                                    {
                                        "id": "event1",
                                        "dateRanges": [
                                            {
                                                "startTime": "12:00",
                                                "endTime": "14:00",
                                                "date": "2025-01-02"
                                            },
                                            {
                                                "startTime": "09:00",
                                                "endTime": "11:00",
                                                "date": "2025-01-01"
                                            }
                                        ]
                                    }
                                  ]
            					}
                            ],
                            "audiences": [],
                            "visibility": "PUBLIC",
                            "status": "Published",
                            "cost": 0.0
                        }]""";

    @Test
    public void testGetLearningPlan() throws Exception {

        String recordResponse = """
                {
                    "content": [
                        {
                            "resourceId": "course2",
                            "recordType": {
                                "type": "COURSE"
                            }
                        },
                        {
                            "resourceId": "course3",
                            "recordType": {
                                "type": "COURSE"
                            }
                        },
                        {
                            "resourceId": "course4",
                            "recordType": {
                                "type": "COURSE"
                            }
                        },
                        {
                            "resourceId": "course5",
                            "recordType": {
                                "type": "COURSE"
                            }
                        }
                    ],
                    "totalPages": 1
                }
                """;

        String moduleRecordResponse = """
                {
                    "moduleRecords": [
                        {
                            "moduleId": "module2",
                            "state": "IN_PROGRESS",
                            "courseId": "course4"
                        },
                        {
                            "moduleId": "module4",
                            "state": "REGISTERED",
                            "eventDate": "2025-01-01T09:00:00",
                            "eventId": "event1",
                            "courseId": "course5"
                        }
                    ]
                }
                """;

        cslStubService.getLearningCatalogue().getMandatoryLearningMap(requiredLearningMap);
        cslStubService.getLearningCatalogue().getCourses(List.of("course3", "course4", "course5"), courses);
        cslStubService.getCsrsStubService().getCivilServant("userId", testDataService.generateCivilServant());
        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module2", "module4"), moduleRecordResponse);
        cslStubService.getLearnerRecord().getLearnerRecords(LearnerRecordQuery.builder().notEventTypes(List.of("COMPLETE_COURSE")).learnerRecordTypes(List.of("COURSE")).build(), 0, recordResponse);
        mockMvc.perform(get("/learning/plan")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.userId").value("userId"))

                .andExpect(jsonPath("$.bookedCourses[0].id").value("course5"))
                .andExpect(jsonPath("$.bookedCourses[0].title").value("Course 5"))
                .andExpect(jsonPath("$.bookedCourses[0].shortDescription").value("Course 5 short description"))
                .andExpect(jsonPath("$.bookedCourses[0].type").value("face-to-face"))
                .andExpect(jsonPath("$.bookedCourses[0].duration").value("240"))
                .andExpect(jsonPath("$.bookedCourses[0].moduleCount").value("1"))
                .andExpect(jsonPath("$.bookedCourses[0].costInPounds").value("0"))
                .andExpect(jsonPath("$.bookedCourses[0].status").value("NULL"))
                .andExpect(jsonPath("$.bookedCourses[0].eventModule.id").value("module4"))
                .andExpect(jsonPath("$.bookedCourses[0].eventModule.title").value("Module 4"))
                .andExpect(jsonPath("$.bookedCourses[0].eventModule.eventId").value("event1"))
                .andExpect(jsonPath("$.bookedCourses[0].eventModule.bookedDate").value("2025-01-01"))
                .andExpect(jsonPath("$.bookedCourses[0].eventModule.dates[0]").value("2025-01-01"))
                .andExpect(jsonPath("$.bookedCourses[0].eventModule.dates[1]").value("2025-01-02"))
                .andExpect(jsonPath("$.bookedCourses[0].eventModule.state").value("REGISTERED"))
                .andExpect(jsonPath("$.bookedCourses[0].canBeMovedToLearningPlan").value("false"))

                .andExpect(jsonPath("$.learningPlanCourses[0].id").value("course4"))
                .andExpect(jsonPath("$.learningPlanCourses[0].title").value("Course 4"))
                .andExpect(jsonPath("$.learningPlanCourses[0].shortDescription").value("Course 4 short description"))
                .andExpect(jsonPath("$.learningPlanCourses[0].type").value("blended"))
                .andExpect(jsonPath("$.learningPlanCourses[0].duration").value("0"))
                .andExpect(jsonPath("$.learningPlanCourses[0].moduleCount").value("2"))
                .andExpect(jsonPath("$.learningPlanCourses[0].costInPounds").value("0"))
                .andExpect(jsonPath("$.learningPlanCourses[0].status").value("IN_PROGRESS"))

                .andExpect(jsonPath("$.learningPlanCourses[1].id").value("course3"))
                .andExpect(jsonPath("$.learningPlanCourses[1].title").value("Course 3"))
                .andExpect(jsonPath("$.learningPlanCourses[1].shortDescription").value("Course 3 short description"))
                .andExpect(jsonPath("$.learningPlanCourses[1].type").value("link"))
                .andExpect(jsonPath("$.learningPlanCourses[1].duration").value("30"))
                .andExpect(jsonPath("$.learningPlanCourses[1].moduleCount").value("1"))
                .andExpect(jsonPath("$.learningPlanCourses[1].costInPounds").value("0"))
                .andExpect(jsonPath("$.learningPlanCourses[1].status").value("NULL"))
        ;

    }
}
