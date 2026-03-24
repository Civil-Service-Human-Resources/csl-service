package uk.gov.cabinetoffice.csl.integration.learnerRecord;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.record.OrganisationalUnitsPagedResponse;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LearnerRecordTest extends IntegrationTestBase {

    @Autowired
    private CSLStubService cslStubService;

    @Autowired
    private TestDataService testDataService;

    String skillsMetadataResponse = """
                {
                    "content":[
                        {
                            "uid": "uid1",
                            "syncTimestamp": "2025-01-01T10:00:00"
                        },
                        {
                            "uid": "uid2",
                            "syncTimestamp": "2024-01-01T10:00:00"
                        },
                        {
                            "uid": "uid3",
                            "syncTimestamp": "2023-01-01T10:00:00"
                        },
                        {
                            "uid": "uid4",
                            "syncTimestamp": "2024-04-01T10:00:00"
                        },
                        {
                            "uid": "uid5",
                            "syncTimestamp": "2025-01-01T10:00:00"
                        },
                        {
                            "uid": "uid6",
                            "syncTimestamp": "2025-01-01T10:00:00"
                        }
                    ],
                    "page": 0,
                    "totalElements": 345
                }
            """;

    @Test
    public void testGetDeltaSkillsLearnerRecord() throws Exception {

        cslStubService.getCsrsStubService().getSkillsMetadata(5, "2022-12-31T10:00", skillsMetadataResponse);

        String input = """
                    {
                        "uids": [
                            "uid1", "uid2", "uid3", "uid4", "uid5", "uid6"
                        ],
                        "emails": null
                    }
                """;
        String uidToEmailMapResponse = """
                    {
                        "uid1": "uid1@email.com",
                        "uid2": "uid2@email.com",
                        "uid3": "uid3@email.com",
                        "uid4": "uid4@email.com",
                        "uid5": "uid5@email.com",
                        "uid6": "uid6@email.com"
                    }
                """;
        cslStubService.getIdentityAPIServiceStubService().getUidToEmailMap(input, uidToEmailMapResponse);

        String expectedLearnerRecordSearchInput = """
                {
                    "learnerIds": ["uid1", "uid2", "uid3", "uid4", "uid5", "uid6"],
                    "createdTimestampGte": "2023-01-01T10:00:00",
                    "updatedTimestampGte": "2023-01-01T10:00:00",
                    "eventTypes" : [ "COMPLETE_COURSE" ],
                    "includeEvents": true
                }
                """;
        String learnerRecordResponse = """
                {
                    "content": [
                        {
                            "resourceId": "courseId",
                            "learnerId": "uid1",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "createdTimestamp" : "2023-01-01T10:00:00",
                            "events": [
                                {
                                    "learnerId": "uid1",
                                    "resourceId": "courseId2",
                                    "eventType": {
                                        "eventType": "COMPLETE_COURSE",
                                        "learnerRecordType": {
                                            "type": "COURSE"
                                        }
                                    },
                                    "eventTimestamp" : "2023-01-01T10:00:00",
                                    "eventSource": {
                                        "source": "csl_source_id"
                                    }
                                }
                            ],
                            "latestEvent": {
                                "learnerId": "uid1",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "COMPLETE_COURSE",
                                    "learnerRecordType": {
                                        "type": "COURSE"
                                    }
                                },
                                "eventTimestamp" : "2023-01-01T10:00:00",
                                "eventSource": {
                                    "source": "csl_source_id"
                                }
                            }
                        },
                        {
                            "resourceId": "courseId2",
                            "learnerId": "uid1",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "createdTimestamp" : "2023-05-01T10:00:00",
                            "events": [
                                {
                                    "learnerId": "uid1",
                                    "resourceId": "courseId2",
                                    "eventType": {
                                        "eventType": "COMPLETE_COURSE",
                                        "learnerRecordType": {
                                            "type": "COURSE"
                                        }
                                    },
                                    "eventTimestamp" : "2023-05-01T10:00:00",
                                    "eventSource": {
                                        "source": "csl_source_id"
                                    }
                                }
                            ],
                            "latestEvent": {
                                "learnerId": "uid1",
                                "resourceId": "courseId2",
                                "eventType": {
                                    "eventType": "COMPLETE_COURSE",
                                    "learnerRecordType": {
                                        "type": "COURSE"
                                    }
                                },
                                "eventTimestamp" : "2023-05-01T10:00:00",
                                "eventSource": {
                                    "source": "csl_source_id"
                                }
                            }
                        }
                    ],
                    "totalPages": 1
                }
                """;
        cslStubService.getLearnerRecord().searchLearnerRecords(expectedLearnerRecordSearchInput, 0, 50, learnerRecordResponse);
        String syncMetadataInput = """
                    {
                        "uids": [
                            "uid1", "uid2", "uid3", "uid4", "uid5", "uid6"
                        ]
                    }
                """;
        cslStubService.getCsrsStubService().syncSkillsMetadata(syncMetadataInput);
        mockMvc.perform(get("/learner-records/skills")
                        .queryParam("mode", "DELTA")
                        .queryParam("size", "5")
                        .queryParam("frequency", "P1Y")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.results.length()").value(2))
                .andExpect(jsonPath("$.results[0].emailAddress").value("uid1@email.com"))
                .andExpect(jsonPath("$.results[0].contentId").value("courseId"))
                .andExpect(jsonPath("$.results[0].enrollmentDate").value("2023-01-01"))
                .andExpect(jsonPath("$.results[0].completionDate").value("2023-01-01"))
                .andExpect(jsonPath("$.results[1].emailAddress").value("uid1@email.com"))
                .andExpect(jsonPath("$.results[1].contentId").value("courseId2"))
                .andExpect(jsonPath("$.results[1].enrollmentDate").value("2023-05-01"))
                .andExpect(jsonPath("$.results[1].completionDate").value("2023-05-01"))
                .andExpect(jsonPath("$.userCount").value(1))
                .andExpect(jsonPath("$.remainingUsers").value(339))
                .andExpect(jsonPath("$.recordCount").value(2));
    }

    @Test
    public void testGetDeltaSkillsLearnerRecordNoUIDsFound() throws Exception {

        cslStubService.getCsrsStubService().getSkillsMetadata(5, "2022-12-31T10:00", skillsMetadataResponse);

        String input = """
                    {
                        "uids": [
                            "uid1", "uid2", "uid3", "uid4", "uid5", "uid6"
                        ],
                        "emails": null
                    }
                """;
        String uidToEmailMapResponse = """
                    { }
                """;
        cslStubService.getIdentityAPIServiceStubService().getUidToEmailMap(input, uidToEmailMapResponse);
        String syncMetadataInput = """
                    {
                        "uids": [
                            "uid1", "uid2", "uid3", "uid4", "uid5", "uid6"
                        ]
                    }
                """;
        cslStubService.getCsrsStubService().syncSkillsMetadata(syncMetadataInput);
        mockMvc.perform(get("/learner-records/skills")
                        .queryParam("mode", "DELTA")
                        .queryParam("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.results.length()").value(0))
                .andExpect(jsonPath("$.userCount").value(0))
                .andExpect(jsonPath("$.remainingUsers").value(339))
                .andExpect(jsonPath("$.recordCount").value(0));
    }

    @Test
    public void testGetDeltaSkillsLearnerRecordNoRecords() throws Exception {

        cslStubService.getCsrsStubService().getSkillsMetadata(5, "2022-12-31T10:00", skillsMetadataResponse);

        String input = """
                    {
                        "uids": [
                            "uid1", "uid2", "uid3", "uid4", "uid5", "uid6"
                        ],
                        "emails": null
                    }
                """;
        String uidToEmailMapResponse = """
                    {
                        "uid1": "uid1@email.com",
                        "uid2": "uid2@email.com",
                        "uid3": "uid3@email.com",
                        "uid4": "uid4@email.com",
                        "uid5": "uid5@email.com"
                    }
                """;
        cslStubService.getIdentityAPIServiceStubService().getUidToEmailMap(input, uidToEmailMapResponse);

        String expectedLearnerRecordSearchInput = """
                {
                    "learnerIds": ["uid1", "uid2", "uid3", "uid4", "uid5"],
                    "createdTimestampGte": "2023-01-01T10:00:00",
                    "updatedTimestampGte": "2023-01-01T10:00:00",
                    "eventTypes" : [ "COMPLETE_COURSE" ],
                    "includeEvents": true
                }
                """;
        String learnerRecordResponse = """
                {
                    "content": [ ],
                    "totalPages": 1
                }
                """;
        cslStubService.getLearnerRecord().searchLearnerRecords(expectedLearnerRecordSearchInput, 0, 50, learnerRecordResponse);
        String syncMetadataInput = """
                    {
                        "uids": [
                            "uid1", "uid2", "uid3", "uid4", "uid5", "uid6"
                        ]
                    }
                """;
        cslStubService.getCsrsStubService().syncSkillsMetadata(syncMetadataInput);
        mockMvc.perform(get("/learner-records/skills")
                        .queryParam("mode", "DELTA")
                        .queryParam("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.results.length()").value(0))
                .andExpect(jsonPath("$.userCount").value(0))
                .andExpect(jsonPath("$.remainingUsers").value(339))
                .andExpect(jsonPath("$.recordCount").value(0));
    }

    @Test
    public void testSearchLearnerRecord() throws Exception {

        OrganisationalUnitsPagedResponse organisationalUnitsPagedResponse = testDataService.generateOrganisationalUnitsPagedResponse();
        cslStubService.getCsrsStubService().getOrganisations(organisationalUnitsPagedResponse);

        String input = """
                    {
                        "emails": [
                            "test1@domain2.com", "test2@domain2.com", "test3@domain2.com", "test4@domain2.com", "test5@domain2.com"
                        ],
                        "uids": null
                    }
                """;
        String emailToUidMapResponse = """
                    {
                        "test1": "test1@domain2.com",
                        "test2": "test2@domain2.com",
                        "test3": "test3@domain2.com",
                        "test4": "test4@domain2.com",
                        "test5": "test5@domain2.com"
                    }
                """;
        cslStubService.getIdentityAPIServiceStubService().getUidToEmailMap(input, emailToUidMapResponse);

        String expectedLearnerRecordSearchInput = """
                {
                    "learnerIds": ["test1", "test2", "test3", "test4", "test5"],
                    "createdTimestampGte": "2023-01-01T10:00:00",
                    "updatedTimestampGte": "2023-01-01T10:00:00",
                    "eventTypes" : [ "COMPLETE_COURSE" ],
                    "includeEvents": true
                }
                """;
        String learnerRecordResponse = """
                {
                    "content": [
                        {
                            "resourceId": "courseId",
                            "learnerId": "test1",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "createdTimestamp" : "2023-01-01T10:00:00",
                            "events": [
                                {
                                    "learnerId": "uid1",
                                    "resourceId": "courseId2",
                                    "eventType": {
                                        "eventType": "COMPLETE_COURSE",
                                        "learnerRecordType": {
                                            "type": "COURSE"
                                        }
                                    },
                                    "eventTimestamp" : "2023-01-01T10:00:00",
                                    "eventSource": {
                                        "source": "csl_source_id"
                                    }
                                }
                            ],
                            "latestEvent": {
                                "learnerId": "test1",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "COMPLETE_COURSE",
                                    "learnerRecordType": {
                                        "type": "COURSE"
                                    }
                                },
                                "eventTimestamp" : "2023-01-01T10:00:00",
                                "eventSource": {
                                    "source": "csl_source_id"
                                }
                            }
                        },
                        {
                            "resourceId": "courseId2",
                            "learnerId": "test1",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "createdTimestamp" : "2023-05-01T10:00:00",
                            "events": [
                                {
                                    "learnerId": "uid1",
                                    "resourceId": "courseId2",
                                    "eventType": {
                                        "eventType": "COMPLETE_COURSE",
                                        "learnerRecordType": {
                                            "type": "COURSE"
                                        }
                                    },
                                    "eventTimestamp" : "2023-05-01T10:00:00",
                                    "eventSource": {
                                        "source": "csl_source_id"
                                    }
                                }
                            ],
                            "latestEvent": {
                                "learnerId": "test1",
                                "resourceId": "courseId2",
                                "eventType": {
                                    "eventType": "COMPLETE_COURSE",
                                    "learnerRecordType": {
                                        "type": "COURSE"
                                    }
                                },
                                "eventTimestamp" : "2023-05-01T10:00:00",
                                "eventSource": {
                                    "source": "csl_source_id"
                                }
                            }
                        }
                    ],
                    "last": true,
                    "number": 0,
                    "totalPages": 1,
                    "totalElements": 2,
                    "size": 2
                }
                """;
        cslStubService.getLearnerRecord().searchLearnerRecords(expectedLearnerRecordSearchInput, 0, 50, learnerRecordResponse);
        mockMvc.perform(post("/learner-records/search")
                        .queryParam("page", "0")
                        .queryParam("size", "50")
                        .header("orgCode", "ON2")
                        .content("""
                                    {
                                        "emails": [
                                            "test1@domain2.com", "test2@domain2.com", "test3@domain2.com", "test4@domain2.com", "test5@domain2.com",
                                             "test6@domain3.com"
                                        ],
                                        "completedDateGte": "2023-01-01T10:00:00"
                                    }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].emailAddress").value("test1@domain2.com"))
                .andExpect(jsonPath("$.content[0].contentId").value("courseId"))
                .andExpect(jsonPath("$.content[0].enrollmentDate").value("2023-01-01"))
                .andExpect(jsonPath("$.content[0].completionDate").value("2023-01-01"))
                .andExpect(jsonPath("$.content[1].emailAddress").value("test1@domain2.com"))
                .andExpect(jsonPath("$.content[1].contentId").value("courseId2"))
                .andExpect(jsonPath("$.content[1].enrollmentDate").value("2023-05-01"))
                .andExpect(jsonPath("$.content[1].completionDate").value("2023-05-01"))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    public void testSearchLearnerRecordInvalidOrgCode() throws Exception {

        OrganisationalUnitsPagedResponse organisationalUnitsPagedResponse = testDataService.generateOrganisationalUnitsPagedResponse();
        cslStubService.getCsrsStubService().getOrganisations(organisationalUnitsPagedResponse);

        mockMvc.perform(post("/learner-records/search")
                        .queryParam("page", "0")
                        .queryParam("size", "50")
                        .header("orgCode", "FAKEORG")
                        .content("""
                                    {
                                        "emails": [
                                            "test1@domain2.com", "test2@domain2.com", "test3@domain2.com", "test4@domain2.com", "test5@domain2.com",
                                             "test6@domain3.com"
                                        ],
                                        "completedDateGte": "2023-01-01T10:00:00"
                                    }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Forbidden exception"))
                .andExpect(jsonPath("$.detail").value("Invalid organisation code provided 'FAKEORG'"));
    }
}
