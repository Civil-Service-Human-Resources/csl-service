package uk.gov.cabinetoffice.csl.integration.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.data.ArrayJsonContentBuilder;
import uk.gov.cabinetoffice.csl.util.data.catalogue.JsonCourseBuilder;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LearningCatalogueTest extends IntegrationTestBase {

    @Autowired
    private CSLStubService cslStubService;

    @Autowired
    private TestDataService testDataService;

    String learningResourceIdsResponse = """
            {
                "content": [
                    "course3",
                    "course4",
                    "course5"
                ],
                "page": 0,
                "size": 200,
                "totalElements": 5,
                "totalPages": 1
            }
            """;

    String audienceMetadataMap = """
            {
                "areasOfWork": {
                    "Analysis": [
                        "course1",
                        "course2"
                    ],
                    "Project delivery": [
                        "course1",
                        "course2"
                    ],
                    "DDaT": [
                        "course7"
                    ]
                },
                "departments": {
                    "CO": [
                        "course1"
                    ],
                    "DWP": [
                        "course8"
                    ]
                },
                "interests": {
                    "EU": [
                        "course9",
                        "course10"
                    ],
                    "Parliament": [
                        "course5"
                    ]
                }
            }
            """;

    @Test
    public void testGetSuggestions() throws Exception {

        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearnerRecord().getLearnerRecordResourceIds(
                LearnerRecordQuery.builder().learnerIds(java.util.Set.of("userId")).build(),
                0,
                200,
                learningResourceIdsResponse);
        cslStubService.getLearningCatalogue().getAudienceMetadataMap(audienceMetadataMap);

        String courses = ArrayJsonContentBuilder.create(
                JsonCourseBuilder.create("course1", "Course 1")
                        .addModule("link", "module1", "module 1", false, 0)
                        .addModule("link", "module2", "module 2", false, 0),
                JsonCourseBuilder.create("course8", "Course 8")
                        .addModule("link", "module3", "module 3", false, 0),
                JsonCourseBuilder.create("course2", "Course 2")
                        .addModule("link", "module4", "module 4", false, 0),
                JsonCourseBuilder.create("course7", "Course 7")
                        .addModule("link", "module5", "module 5", false, 0)
                        .addModule("link", "module6", "module 6", false, 0)
                        .addModule("link", "module7", "module 7", false, 0),
                JsonCourseBuilder.create("course9", "Course 9")
                        .addModule("link", "module8", "module 8", false, 0),
                JsonCourseBuilder.create("course10", "Course 10")
                        .addModule("link", "module9", "module 9", false, 0)
        ).get().toString();

        cslStubService.getLearningCatalogue().getCourses(List.of("course1", "course8", "course2", "course7", "course9", "course10"), courses);

        mockMvc.perform(get("/learning/catalogue/suggestions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json("""
                        {
                            "suggestions": [
                                {
                                    "title": "Cabinet Office",
                                    "courses": [
                                        {
                                            "id": "course1",
                                            "title": "Course 1",
                                            "shortDescription": "Course 1 short description",
                                            "type": "blended",
                                            "duration": 0,
                                            "moduleCount": 2,
                                            "costInPounds": 0,
                                            "status": "NULL"
                                        },
                                        {
                                            "id": "course8",
                                            "title": "Course 8",
                                            "shortDescription": "Course 8 short description",
                                            "type": "link",
                                            "duration": 0,
                                            "moduleCount": 1,
                                            "costInPounds": 0,
                                            "status": "NULL"
                                        }
                                    ]
                                },
                                {
                                    "title": "DDaT",
                                    "courses": [
                                        {
                                            "id": "course7",
                                            "title": "Course 7",
                                            "shortDescription": "Course 7 short description",
                                            "type": "blended",
                                            "duration": 0,
                                            "moduleCount": 3,
                                            "costInPounds": 0,
                                            "status": "NULL"
                                        }
                                    ]
                                },
                                {
                                    "title": "Analysis",
                                    "courses": [
                                        {
                                            "id": "course2",
                                            "title": "Course 2",
                                            "shortDescription": "Course 2 short description",
                                            "type": "link",
                                            "duration": 0,
                                            "moduleCount": 1,
                                            "costInPounds": 0,
                                            "status": "NULL"
                                        }
                                    ]
                                },
                                {
                                    "title": "EU",
                                    "courses": [
                                        {
                                            "id": "course10",
                                            "title": "Course 10",
                                            "shortDescription": "Course 10 short description",
                                            "type": "link",
                                            "duration": 0,
                                            "moduleCount": 1,
                                            "costInPounds": 0,
                                            "status": "NULL"
                                        },
                                        {
                                            "id": "course9",
                                            "title": "Course 9",
                                            "shortDescription": "Course 9 short description",
                                            "type": "link",
                                            "duration": 0,
                                            "moduleCount": 1,
                                            "costInPounds": 0,
                                            "status": "NULL"
                                        }
                                    ]
                                }
                            ]
                        }
                        """, true));
    }

}
