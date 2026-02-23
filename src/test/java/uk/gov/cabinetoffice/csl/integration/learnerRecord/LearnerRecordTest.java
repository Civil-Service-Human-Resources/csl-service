package uk.gov.cabinetoffice.csl.integration.learnerRecord;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LearnerRecordTest extends IntegrationTestBase {

    @Autowired
    private CSLStubService cslStubService;

    @Autowired
    private TestDataService testDataService;

    @Test
    public void testGetDeltaSkillsLearnerRecord() throws Exception {
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
                            }
                        ],
                        "page": 0,
                        "totalElements": 345
                    }
                """;

        cslStubService.getCsrsStubService().getSkillsMetadata(5, true, skillsMetadataResponse);

        String input = """
                    {
                        "uids": [
                            "uid1", "uid2", "uid3", "uid4", "uid5"
                        ]
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
                    "updatedTimestampGte": "2023-01-01T10:00:00"
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
                        },
                        {
                            "resourceId": "courseId",
                            "learnerId": "uid2",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "createdTimestamp" : "2024-01-01T10:00:00",
                            "latestEvent": {
                                "learnerId": "uid2",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "MOVE_TO_LEARNING_PLAN",
                                    "learnerRecordType": {
                                        "type": "COURSE"
                                    }
                                },
                                "eventTimestamp" : "2024-01-01T10:00:00",
                                "eventSource": {
                                    "source": "csl_source_id"
                                }
                            }
                        },
                        {
                            "resourceId": "courseId",
                            "learnerId": "uid4",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "createdTimestamp" : "2025-01-01T10:00:00",
                            "latestEvent": {
                                "learnerId": "uid4",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "MOVE_TO_LEARNING_PLAN",
                                    "learnerRecordType": {
                                        "type": "COURSE"
                                    }
                                },
                                "eventTimestamp" : "2025-01-01T10:00:00",
                                "eventSource": {
                                    "source": "csl_source_id"
                                }
                            }
                        },
                        {
                            "resourceId": "courseId",
                            "learnerId": "uid5",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "createdTimestamp" : "2024-05-01T10:00:00",
                            "latestEvent": {
                                "learnerId": "uid5",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "MOVE_TO_LEARNING_PLAN",
                                    "learnerRecordType": {
                                        "type": "COURSE"
                                    }
                                },
                                "eventTimestamp" : "2024-05-01T10:00:00",
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
                            "uid1", "uid2", "uid3", "uid4", "uid5"
                        ]
                    }
                """;
        cslStubService.getCsrsStubService().syncSkillsMetadata(syncMetadataInput);
        mockMvc.perform(get("/learner-records/skills")
                        .queryParam("mode", "DELTA")
                        .queryParam("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }
}
