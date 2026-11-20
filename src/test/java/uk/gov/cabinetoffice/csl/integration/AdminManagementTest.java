package uk.gov.cabinetoffice.csl.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.data.ArrayJsonContentBuilder;
import uk.gov.cabinetoffice.csl.util.data.catalogue.DateRangeJsonValues;
import uk.gov.cabinetoffice.csl.util.data.catalogue.JsonCourseBuilder;
import uk.gov.cabinetoffice.csl.util.data.catalogue.JsonModuleBuilder;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class AdminManagementTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CSLStubService cslStubService;

    private String jsonCourse;

    @BeforeEach
    public void before() {
        jsonCourse = ArrayJsonContentBuilder.create(JsonCourseBuilder.create("course1", "A Course 1")
                        .addModule("link", "module1", "module 1", false, 0)
                        .addModule("elearning", "module2", "module 2", true, 100)
                        .addFileModule("module3", "module 3", false, 200)
                        .addModule(JsonModuleBuilder.buildFaceToFace("module4", "course1",
                                "Face to face module", 300, BigDecimal.valueOf(100L), "event1",
                                new DateRangeJsonValues("09:00", "11:00", "2023-01-01"),
                                new DateRangeJsonValues("09:00", "11:00", "2023-03-01")).get())
                        .addLearningTag(1L, "Project Management", "project-management")
                        .addAudience(new String[]{"AA", "AO", "G7"}, new String[]{"Commercial", "Finance"}, new String[]{"EU and International"})
                        .addDepartmentRequiredLearning("ON2", "2023-05-05"))
                .build();
    }

    @Test
    public void testRenderEventOverview() throws Exception {
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), jsonCourse);
        String eventDtoResponse = """
                {
                    "uid": "eventId",
                    "status": "Active",
                    "activeBookings": [
                        {
                            "id": 1,
                            "learner": "learner1",
                            "status": "Requested",
                            "bookingReference": "ABC12",
                            "poNumber": "PO123"
                        },
                        {
                            "id": 2,
                            "learner": "learner2",
                            "status": "Confirmed",
                            "bookingReference": "DEF12",
                            "poNumber": "PO456"
                        },
                        {
                            "id": 3,
                            "learner": "learner3",
                            "status": "Confirmed",
                            "bookingReference": "GHI12",
                            "poNumber": "PO789"
                        }
                    ],
                    "invites": [
                        {
                            "learnerEmail": "learner1@email.com"
                        }
                    ]
                }
                """;
        cslStubService.getLearnerRecord().findEvent("event1", true, true, eventDtoResponse);
        cslStubService.getIdentityAPIServiceStubService().getIdentityMap(List.of("learner1", "learner2", "learner3"),
                """
                        {
                         "learner1": {
                             "username": "learner1@email.com"
                         },
                         "learner2": {
                             "username": "learner2@email.com"
                         }
                        }
                        """);
        mockMvc.perform(get("/admin/management/courses/course1/modules/module4/events/event1/overview"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(
                        """
                                {
                                    "id": "event1",
                                    "venue": {
                                        "location": "London",
                                        "address": "1 London Road",
                                        "capacity": 10,
                                        "minCapacity": 5,
                                        "availability": 7
                                    },
                                    "dates": [
                                        "01 Jan 2023",
                                        "01 Mar 2023"
                                    ],
                                    "status": "Active",
                                    "moduleId": "module4",
                                    "moduleTitle": "Face to face module",
                                    "courseId": "course1",
                                    "courseTitle": "A Course 1",
                                    "courseStatus": "Published",
                                    "invitedEmails": [
                                        "learner1@email.com"
                                    ],
                                    "bookings": [
                                        {
                                            "id": 1,
                                            "reference": "ABC12",
                                            "learnerEmail": "learner1@email.com",
                                            "status": "Requested",
                                            "poNumber": "PO123"
                                        },
                                        {
                                            "id": 2,
                                            "reference": "DEF12",
                                            "learnerEmail": "learner2@email.com",
                                            "status": "Confirmed",
                                            "poNumber": "PO456"
                                        }
                                    ]
                                }
                                """
                ));
    }

    @Test
    public void testRenderCourseOverview() throws Exception {
        cslStubService.getCsrsStubService().getGradesDefaultResponse();
        cslStubService.getCsrsStubService().getOrganisations(testDataService.generateOrganisationalUnitsPagedResponse());
        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), jsonCourse);
        mockMvc.perform(get("/admin/management/courses/course1/overview"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(
                        """
                                {
                                    "id": "course1",
                                    "title": "A Course 1",
                                    "description": "A Course 1",
                                    "learningOutcomes": "A Course 1 learning outcomes",
                                    "status": "PUBLISHED",
                                    "visibility": "PUBLIC",
                                    "type": "blended",
                                    "duration": 14700,
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
                                    "costInPounds": 100,
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
                                            "state": null,
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
                                            "state": null,
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
                                            "state": null,
                                            "mustConfirmBooking": false
                                        },
                                        {
                                            "id": "module4",
                                            "title": "Face to face module",
                                            "description": "Face to face module",
                                            "mandatory": true,
                                            "associatedLearning": false,
                                            "type": "face-to-face",
                                            "duration": 14400,
                                            "costInPounds": 100,
                                            "state": null,
                                            "mustConfirmBooking": false
                                        }
                                    ],
                                    "mandatoryModuleCount": 0,
                                    "nextAvailableDate": "1 Jan 2023",
                                    "faceToFaceModuleOverviews": [
                                        {
                                            "id": "module4",
                                            "title": "Face to face module",
                                            "description": "Face to face module",
                                            "mandatory": true,
                                            "associatedLearning": false,
                                            "type": "face-to-face",
                                            "duration": 14400,
                                            "costInPounds": 100,
                                            "mustConfirmBooking": false,
                                            "earliestDate": "2023-01-01T09:00:00",
                                            "events": [
                                                {
                                                    "id": "event1",
                                                    "venue": {
                                                        "location": "London",
                                                        "address": "1 London Road",
                                                        "capacity": 10,
                                                        "minCapacity": 5,
                                                        "availability": null
                                                    },
                                                    "dates": [
                                                        "01 Jan 2023",
                                                        "01 Mar 2023"
                                                    ]
                                                }
                                            ]
                                        }
                                    ],
                                    "audiences": [
                                        {
                                            "audienceName": "Blank audience name",
                                            "audienceType": "OPEN",
                                            "organisations": [],
                                            "grades": [
                                                "Administrative assistant",
                                                "Administrative officer"
                                            ],
                                            "areasOfWork": [
                                                "Commercial",
                                                "Finance"
                                            ],
                                            "interests": [
                                                "EU and International"
                                            ],
                                            "requiredBy": ""
                                        },
                                        {
                                            "audienceName": "Required audience name",
                                            "audienceType": "REQUIRED_LEARNING",
                                            "organisations": [
                                                "OrgName1 (OName1) | OrgName2"
                                            ],
                                            "grades": [],
                                            "areasOfWork": [],
                                            "interests": [],
                                            "requiredBy": "5 May 2023"
                                        }
                                    ]
                                }
                                """, true
                ));
    }
}
