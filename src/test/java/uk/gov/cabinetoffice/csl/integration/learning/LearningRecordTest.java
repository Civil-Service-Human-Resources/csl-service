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

    String courses = """
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
                                              "moduleType": "link",
                                              "duration": 30
                                      }
                              ],
                              "audiences": [
                                      {
                                              "id": "DWP",
                                              "name": "DWP",
                                              "areasOfWork": [],
                                              "departments": ["DWP"],
                                              "grades": [],
                                              "interests": [],
                                              "requiredBy": "2024-01-01T00:00:00Z",
                                              "frequency": "P1Y",
                                              "type": "REQUIRED_LEARNING",
                                              "eventId": null
                                      },
                                      {
                                              "id": "HMRC",
                                              "name": "HMRC",
                                              "areasOfWork": [],
                                              "departments": ["HMRC"],
                                              "grades": [],
                                              "interests": [],
                                              "requiredBy": "2023-01-01T00:00:00Z",
                                              "frequency": "P1Y",
                                              "type": "REQUIRED_LEARNING",
                                              "eventId": null
                                      }
                              ],
                              "visibility": "PUBLIC",
                              "status": "Published",
                              "cost": 0.0
                          },{
                              "id": "course2",
                              "title": "Course 2",
                              "shortDescription": "Course 2",
                              "description": "Course 2",
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
                                              "type": "file",
                                              "url": "https://www.gov.uk/",
                                              "id": "module2",
                                              "title": "module2",
                                              "description": "module2",
                                              "optional": false,
                                              "moduleType": "file"
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
                          },{
                            "id": "course3",
                            "title": "Course 3",
                            "shortDescription": "Course 3",
                            "description": "Course 3",
                            "modules": [
                                {
                                  "type": "face-to-face",
                                  "id": "module1",
                                  "title": "module1",
                                  "description": "module1",
                                  "optional": false,
                                  "moduleType": "face-to-face",
                                  "events": [
                                    {
                                        "dateRanges": [
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
                            "audiences": [
                                    {
                                        "id": "aud1",
                                        "name": "audience1",
                                        "areasOfWork": [],
                                        "departments": [],
                                        "grades": [],
                                        "interests": [],
                                        "type": "OPEN",
                                        "eventId": null
                                    }
                            ],
                            "visibility": "PUBLIC",
                            "status": "Published",
                            "cost": 0.0
                        }]""";

    @Test
    public void testGetLearningRecord() throws Exception {

        String eventsResponse = """
                {
                    "content": [
                        {
                            "eventTimestamp": "2025-01-01T00:00:00Z",
                            "resourceId": "course1"
                        },
                        {
                            "eventTimestamp": "2026-01-01T00:00:00Z",
                            "resourceId": "course1"
                        },
                        {
                            "eventTimestamp": "2023-01-01T00:00:00Z",
                            "resourceId": "course2"
                        },
                        {
                            "eventTimestamp": "2025-01-01T00:00:00Z",
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
                .andExpect(jsonPath("$.requiredLearningRecord.completedCourses[0].completionDate").value("2026-01-01T00:00:00"))
                .andExpect(jsonPath("$.otherLearning[0].id").value("course3"))
                .andExpect(jsonPath("$.otherLearning[0].title").value("Course 3"))
                .andExpect(jsonPath("$.otherLearning[0].type").value("face-to-face"))
                .andExpect(jsonPath("$.otherLearning[0].duration").value(7200))
                .andExpect(jsonPath("$.otherLearning[0].completionDate").value("2025-01-01T00:00:00"));

    }
}
