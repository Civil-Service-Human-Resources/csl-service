package uk.gov.cabinetoffice.csl.integration.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEventQuery;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserLearningTest extends IntegrationTestBase {

    @Autowired
    private CSLStubService cslStubService;

    @Autowired
    private TestDataService testDataService;

    @Test
    public void testGetLearning() throws Exception {
        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);

        String requiredLearningMap = """
            {
                "departmentCodeMap": {
                    "CO": ["course1", "course2"]
                }
            }
            """;
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(requiredLearningMap);

        String learnerRecordsResponse = """
            {
                "content": [
                    {
                        "resourceId": "course4",
                        "recordType": {
                            "type": "COURSE"
                        },
                        "latestEvent": {
                            "learnerId": "userId",
                            "resourceId": "course4",
                            "eventType": {
                                "eventType": "COMPLETE_COURSE",
                                "learnerRecordType": {
                                    "type": "COURSE"
                                }
                            },
                            "eventTimestamp": "2026-01-01T10:00:00Z",
                            "eventSource": {
                                "source": "csl_source_id"
                            }
                        }
                    },
                    {
                        "resourceId": "course3",
                        "recordType": {
                            "type": "COURSE"
                        }
                    }
                ],
                "page": 0,
                "size": 20,
                "totalElements": 2,
                "totalPages": 1
            }
            """;

        cslStubService.getLearnerRecord().getLearnerRecordPage(
                LearnerRecordQuery.builder().learnerIds(java.util.Set.of("userId")).notResourceIds(List.of("course1", "course2")).build(),
                0,
                20,
                learnerRecordsResponse);

        String courses = """
            [
              {
                "id": "course3",
                "title": "B Course 3"
              },
              {
                "id": "course4",
                "title": "A Course 4"
              }
            ]
            """;

        cslStubService.getLearningCatalogue().getCourses(List.of("course4", "course3"), courses);

        mockMvc.perform(get("/learning/userId?page=0&size=20").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.learning.length()").value(2))
                .andExpect(jsonPath("$.learning[0].title").value("A Course 4"))
                .andExpect(jsonPath("$.learning[0].status").value("Completed"))
                .andExpect(jsonPath("$.learning[0].completionDate").value("1 Jan 2026"))
                .andExpect(jsonPath("$.learning[1].title").value("B Course 3"))
                .andExpect(jsonPath("$.learning[1].status").value(""))
                .andExpect(jsonPath("$.learning[1].completionDate").isEmpty())
                .andExpect(jsonPath("$.totalResults").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    public void testGetDetailedLearning() throws Exception {
        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);

        String courses = """
            [
              {
                "id": "course1",
                "title": "Course 1",
                "shortDescription": "Course 1 short description",
                "description": "Course 1 description",
                "modules": [
                  {
                    "type": "link",
                    "url": "https://www.gov.uk/",
                    "id": "module1",
                    "title": "Module 1",
                    "description": "Module 1 description",
                    "optional": false,
                    "moduleType": "link",
                    "cost": 0.0
                  }
                ],
                "audiences": [],
                "visibility": "PUBLIC",
                "status": "Published",
                "cost": 0.0
              }
            ]
            """;

        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courses);

        cslStubService.getLearningCatalogue().getMandatoryLearningMap("""
            {
                "departmentCodeMap": {
                    "CO": []
                }
            }
            """);

        cslStubService.getLearnerRecord().getLearnerRecordEvents(0, LearnerRecordEventQuery.builder().userId("userId").eventTypes(List.of("COMPLETE_COURSE")).build(), """
            {
                "content": [
                    {
                        "eventTimestamp": "2026-01-01T10:00:00Z",
                        "resourceId": "course1"
                    }
                ],
                "totalPages": 1
            }
            """);

        String learnerRecordsResponse = """
            {
                "content": [
                    {
                        "resourceId": "course1",
                        "learnerId": "userId",
                        "recordType": {
                            "type": "COURSE"
                        },
                        "latestEvent": {
                            "learnerId": "userId",
                            "resourceId": "course1",
                            "eventType": {
                                "eventType": "COMPLETE_COURSE",
                                "learnerRecordType": {
                                    "type": "COURSE"
                                }
                            },
                            "eventTimestamp": "2026-01-01T10:00:00Z",
                            "eventSource": {
                                "source": "csl_source_id"
                            }
                        }
                    }
                ],
                "totalPages": 1
            }
            """;

        LearnerRecordQuery query = LearnerRecordQuery.builder()
                .learnerIds(Set.of("userId"))
                .resourceIds(Set.of("course1"))
                .build();

        cslStubService.getLearnerRecord().getLearnerRecords(query, 0, learnerRecordsResponse);

        String moduleRecordResponse = """
                {
                    "moduleRecords": [
                        {
                            "userId": "userId",
                            "moduleId": "module1",
                            "state": "COMPLETED",
                            "updatedAt": "2025-01-01T09:00:00",
                            "courseId": "course1"
                        }
                    ]
                }""";

        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1"), moduleRecordResponse);

        mockMvc.perform(get("/learning/detailed/userId?courseIds=course1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].courseId").value("course1"))
                .andExpect(jsonPath("$.courses[0].courseTitle").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$.courses[0].audience").doesNotExist());
    }
}
