package uk.gov.cabinetoffice.csl.integration.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

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

    String courses = """
            [
              {
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
              },
              {
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
              },
              {
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
                            "date": "2022-01-02"
                          },
                          {
                            "startTime": "09:00",
                            "endTime": "11:00",
                            "date": "2022-01-01"
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
              },
              {
                "id": "course6",
                "title": "Course 6",
                "shortDescription": "Course 6 short description",
                "description": "Course 6 description",
                "modules": [
                  {
                    "type": "elearning",
                    "url": "https://www.gov.uk/",
                    "id": "module5",
                    "title": "Module 5",
                    "description": "Module 5 description",
                    "optional": false,
                    "moduleType": "elearning",
                    "cost": 0.0
                  }
                ],
                "audiences": [],
                "visibility": "PUBLIC",
                "status": "Published",
                "cost": 0.0
              },
              {
                "id": "course7",
                "title": "Course 7",
                "shortDescription": "Course 7 short description",
                "description": "Course 6 description",
                "modules": [
                  {
                    "type": "elearning",
                    "url": "https://www.gov.uk/",
                    "id": "module6",
                    "title": "Module 6",
                    "description": "Module 6 description",
                    "optional": false,
                    "moduleType": "elearning",
                    "cost": 0.0
                  }
                ],
                "audiences": [],
                "visibility": "PUBLIC",
                "status": "Published",
                "cost": 0.0
              },
              {
                "id": "course8",
                "title": "Course 8",
                "shortDescription": "Course 8 short description",
                "description": "Course 8 description",
                "modules": [
                  {
                    "type": "face-to-face",
                    "id": "module7",
                    "title": "Module 7",
                    "description": "Module 7 description",
                    "optional": false,
                    "moduleType": "face-to-face",
                    "cost": 0.0,
                    "events": [
                      {
                        "id": "event2",
                        "dateRanges": [
                          {
                            "startTime": "12:00",
                            "endTime": "14:00",
                            "date": "2022-01-02"
                          },
                          {
                            "startTime": "09:00",
                            "endTime": "11:00",
                            "date": "2022-01-01"
                          }
                        ]
                      }
                    ]
                  },
                  {
                    "type": "elearning",
                    "url": "https://www.gov.uk/",
                    "id": "module8",
                    "title": "Module 8",
                    "description": "Module 8 description",
                    "optional": false,
                    "moduleType": "elearning",
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

    @Test
    public void testGetLearningPlanWhenHomepageCompleteLearningPlanCoursesIsFalse() throws Exception {

        String recordResponse = """
                {
                    "content": [
                        {
                            "resourceId": "course2",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": {
                                "learnerId": "userId",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "MOVE_TO_LEARNING_PLAN",
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
                            "resourceId": "course3",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": {
                                "learnerId": "userId",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "MOVE_TO_LEARNING_PLAN",
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
                            "resourceId": "course4",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": {
                                "learnerId": "userId",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "MOVE_TO_LEARNING_PLAN",
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
                            "resourceId": "course5",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": {
                                "learnerId": "userId",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "MOVE_TO_LEARNING_PLAN",
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
                            "resourceId": "course6",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": {
                                "learnerId": "userId",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "REMOVE_FROM_LEARNING_PLAN",
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
                            "resourceId": "course7",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": null
                        }
                        ,
                        {
                            "resourceId": "course8",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": null
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
                            "updatedAt": "2025-01-01T09:00:00",
                            "courseId": "course4"
                        },
                        {
                            "moduleId": "module4",
                            "state": "APPROVED",
                            "eventDate": "2022-01-02T09:00:00",
                            "eventId": "event1",
                            "updatedAt": "2022-01-02T09:00:00",
                            "courseId": "course5"
                        },
                        {
                            "moduleId": "module5",
                            "state": "IN_PROGRESS",
                            "updatedAt": "2025-01-01T09:00:00",
                            "courseId": "course6"
                        },
                        {
                            "moduleId": "module6",
                            "state": "IN_PROGRESS",
                            "updatedAt": "2025-01-01T09:00:00",
                            "courseId": "course7"
                        },
                        {
                            "moduleId": "module7",
                            "state": "APPROVED",
                            "eventDate": "2022-01-01T09:00:00",
                            "eventId": "event2",
                            "updatedAt": "2022-01-01T09:00:00",
                            "courseId": "course8"
                        }
                    ]
                }""";

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

        mockMvc.perform(get("/learning/plan?HOMEPAGE_COMPLETE_LEARNING_PLAN_COURSES=false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(expectedJson, true));

    }

    @Test
    public void testGetLearningPlanWhenHomepageCompleteLearningPlanCoursesIsTrue() throws Exception {

        String recordResponse = """
                {
                    "content": [
                        {
                            "resourceId": "course2",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": {
                                "learnerId": "userId",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "MOVE_TO_LEARNING_PLAN",
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
                            "resourceId": "course3",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": {
                                "learnerId": "userId",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "MOVE_TO_LEARNING_PLAN",
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
                            "resourceId": "course4",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": {
                                "learnerId": "userId",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "MOVE_TO_LEARNING_PLAN",
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
                            "resourceId": "course5",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": {
                                "learnerId": "userId",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "MOVE_TO_LEARNING_PLAN",
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
                            "resourceId": "course6",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": {
                                "learnerId": "userId",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "REMOVE_FROM_LEARNING_PLAN",
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
                            "resourceId": "course7",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": null
                        },
                        {
                            "resourceId": "course8",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": null
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
                            "updatedAt": "2025-01-01T09:00:00",
                            "courseId": "course4"
                        },
                        {
                            "moduleId": "module4",
                            "state": "APPROVED",
                            "eventDate": "2022-01-02T09:00:00",
                            "eventId": "event1",
                            "updatedAt": "2022-01-02T09:00:00",
                            "courseId": "course5"
                        },
                        {
                            "moduleId": "module5",
                            "state": "IN_PROGRESS",
                            "updatedAt": "2025-01-01T09:00:00",
                            "courseId": "course6"
                        },
                        {
                            "moduleId": "module6",
                            "state": "IN_PROGRESS",
                            "updatedAt": "2025-01-01T09:00:00",
                            "courseId": "course7"
                        },
                        {
                            "moduleId": "module7",
                            "state": "APPROVED",
                            "eventDate": "2022-01-01T09:00:00",
                            "eventId": "event2",
                            "updatedAt": "2022-01-01T09:00:00",
                            "courseId": "course8"
                        }
                    ]
                }""";

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

        String courses47 = """
            [
              {
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
              },
              {
                "id": "course7",
                "title": "Course 7",
                "shortDescription": "Course 7 short description",
                "description": "Course 6 description",
                "modules": [
                  {
                    "type": "elearning",
                    "url": "https://www.gov.uk/",
                    "id": "module6",
                    "title": "Module 6",
                    "description": "Module 6 description",
                    "optional": false,
                    "moduleType": "elearning",
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

        String recordResponse = """
                {
                    "content": [
                        {
                            "resourceId": "course4",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": {
                                "learnerId": "userId",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "MOVE_TO_LEARNING_PLAN",
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
                            "resourceId": "course7",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": null
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
                            "state": "COMPLETED",
                            "updatedAt": "2025-01-01T09:00:00",
                            "courseId": "course4"
                        },
                        {
                            "moduleId": "module3",
                            "state": "COMPLETED",
                            "updatedAt": "2025-01-01T10:00:00",
                            "courseId": "course4"
                        },
                        {
                            "moduleId": "module6",
                            "state": "IN_PROGRESS",
                            "updatedAt": "2025-01-01T09:00:00",
                            "courseId": "course7"
                        }
                    ]
                }""";

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

        String courses47 = """
            [
              {
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
              },
              {
                "id": "course7",
                "title": "Course 7",
                "shortDescription": "Course 7 short description",
                "description": "Course 6 description",
                "modules": [
                  {
                    "type": "elearning",
                    "url": "https://www.gov.uk/",
                    "id": "module6",
                    "title": "Module 6",
                    "description": "Module 6 description",
                    "optional": false,
                    "moduleType": "elearning",
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

        String recordResponse = """
                {
                    "content": [
                        {
                            "resourceId": "course4",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": {
                                "learnerId": "userId",
                                "resourceId": "courseId",
                                "eventType": {
                                    "eventType": "MOVE_TO_LEARNING_PLAN",
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
                            "resourceId": "course7",
                            "recordType": {
                                "type": "COURSE"
                            },
                            "latestEvent": null
                        }
                    ],
                    "totalPages": 1
                }
                """;

        String moduleRecordResponse = """
                {
                    "moduleRecords": [
                        {
                            "id": 2,
                            "uid": "module2",
                            "userId": "userId",
                            "courseId": "course4",
                            "moduleId": "module2",
                            "moduleTitle": "module2",
                            "moduleType": "link",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2025-01-01T10:00:00",
                            "createdAt": "2024-01-01T10:00:00",
                            "updatedAt": "2025-01-01T10:00:00"
                        },
                        {
                            "id": 3,
                            "uid": "module3",
                            "userId": "userId",
                            "courseId": "course4",
                            "moduleId": "module3",
                            "moduleTitle": "module3",
                            "moduleType": "file",
                            "duration": 3600,
                            "state": "COMPLETED",
                            "completionDate": "2025-01-01T10:00:00",
                            "createdAt": "2024-01-01T10:00:00",
                            "updatedAt": "2025-01-01T10:00:00"
                        },
                        {
                            "id": 6,
                            "uid": "module6",
                            "userId": "userId",
                            "courseId": "course7",
                            "moduleId": "module6",
                            "moduleTitle": "module6",
                            "moduleType": "elearning",
                            "duration": 3600,
                            "state": "IN_PROGRESS",
                            "createdAt": "2024-01-01T10:00:00",
                            "updatedAt": "2025-01-01T10:00:00"
                        }
                    ]
                }
                """;

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
