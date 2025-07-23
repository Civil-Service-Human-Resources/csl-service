package uk.gov.cabinetoffice.csl.integration.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEventQuery;
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
                              "shortDescription": "Course 3",
                              "description": "Course 3",
                              "modules": [
                                      {
                                              "type": "link",
                                              "url": "https://www.gov.uk/",
                                              "id": "module1",
                                              "title": "module1",
                                              "description": "module1",
                                              "optional": false,
                                              "moduleType": "link",
                                              "duration": 30
                                      }
                              ],
                              "audiences": [],
                              "visibility": "PUBLIC",
                              "status": "Published",
                              "cost": 0.0
                          },{
                              "id": "course4",
                              "title": "Course 4",
                              "shortDescription": "Course 4",
                              "description": "Course 4",
                              "modules": [
                                      {
                                              "type": "link",
                                              "url": "https://www.gov.uk/",
                                              "id": "module2",
                                              "title": "module2",
                                              "description": "module2",
                                              "optional": false,
                                              "moduleType": "link"
                                      },
                                    {
                                              "type": "file",
                                              "url": "https://www.gov.uk/",
                                              "id": "module3",
                                              "title": "module3",
                                              "description": "module3",
                                              "optional": false,
                                              "moduleType": "file"
                                      }
                              ],
                              "audiences": [],
                              "visibility": "PUBLIC",
                              "status": "Published",
                              "cost": 0.0
                          },{
                            "id": "course5",
                            "title": "Course 5",
                            "shortDescription": "Course 5",
                            "description": "Course 5",
                            "modules": [
                                {
                                  "type": "face-to-face",
                                  "id": "module4",
                                  "title": "module4",
                                  "description": "module4",
                                  "optional": false,
                                  "moduleType": "face-to-face",
                                  "events": [
                                    {
                                        "id": "event1",
                                        "dateRanges": [
                                            {
                                                "startTime": "09:00",
                                                "endTime": "11:00",
                                                "date": "2025-01-01"
                                            },
                                            {
                                                "startTime": "12:00",
                                                "endTime": "14:00",
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

        String eventsResponse = """
                {
                    "content": [
                        {
                            "eventTimestamp": "2024-01-01T00:00:00Z",
                            "resourceId": "course3"
                        },
                        {
                            "eventTimestamp": "2024-01-01T00:00:00Z",
                            "resourceId": "course4"
                        },
                        {
                            "eventTimestamp": "2024-01-01T00:00:00Z",
                            "resourceId": "course5"
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
                .andExpect(jsonPath("$.requiredLearningRecord.completedCourses[0].completionDate").value("2026-01-01T00:00:00"))
                .andExpect(jsonPath("$.otherLearning[0].id").value("course3"))
                .andExpect(jsonPath("$.otherLearning[0].title").value("Course 3"))
                .andExpect(jsonPath("$.otherLearning[0].type").value("face-to-face"))
                .andExpect(jsonPath("$.otherLearning[0].duration").value(120))
                .andExpect(jsonPath("$.otherLearning[0].completionDate").value("2025-01-01T00:00:00"));

    }
}
