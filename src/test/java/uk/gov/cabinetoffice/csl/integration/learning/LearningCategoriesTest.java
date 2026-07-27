package uk.gov.cabinetoffice.csl.integration.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.data.ArrayJsonContentBuilder;
import uk.gov.cabinetoffice.csl.util.data.catalogue.JsonLearningTagBuilder;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LearningCategoriesTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CSLStubService cslStubService;

    private final String learningTagsPagedResponse = new ArrayJsonContentBuilder<JsonLearningTagBuilder>()
            .addElements(
                    JsonLearningTagBuilder.create(1L, null, null, "2025-01-01T10:00:00").isCategory(),
                    JsonLearningTagBuilder.create(2L, 1L, "TagName1", "2025-01-01T10:00:00"),
                    JsonLearningTagBuilder.create(3L, 2L, "TagName2", "2025-01-01T10:00:00"),
                    JsonLearningTagBuilder.create(4L, null, null, "2025-01-01T10:00:00").isCategory(),
                    JsonLearningTagBuilder.create(5L, 1L, "TagName1", "2025-01-01T10:00:00"),
                    JsonLearningTagBuilder.create(6L, null, null, "2025-01-01T10:00:00").isArchived()
            ).getAsPaginatedAndBuild(0, 5, 1);


    @Test
    public void testGetCategories() throws Exception {
        cslStubService.getLearningCatalogue().getLearningTags(learningTagsPagedResponse);
        mockMvc.perform(get("/learning/categories"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json("""
                        {
                          "categories": [
                            {
                              "title": "TagName1",
                              "description": "TagName1 description",
                              "url": "TAGN1"
                            },
                            {
                              "title": "TagName4",
                              "description": "TagName4 description",
                              "url": "TAGN4"
                            }
                          ]
                        }
                        """));
    }

    @Test
    public void testGetSubCategories() throws Exception {
        cslStubService.getLearningCatalogue().getLearningTags(learningTagsPagedResponse);
        mockMvc.perform(get("/learning/categories/TAGN2"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json("""
                        {
                            "categories": [
                                {
                                    "title": "TagName3",
                                    "description": "TagName3 description",
                                    "url": "TAGN3"
                                }
                            ],
                            "title": "TagName2",
                            "description": "TagName2 description",
                            "parents": [
                                {
                                    "text": "TagName1",
                                    "link": "TAGN1"
                                }
                            ]
                        }
                        """));
    }

    @Test
    public void testGetSubCategoriesDescendant() throws Exception {
        cslStubService.getLearningCatalogue().getLearningTags(learningTagsPagedResponse);
        mockMvc.perform(get("/learning/categories/TAGN3"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json("""
                        {
                            "categories": [],
                            "title": "TagName3",
                            "description": "TagName3 description",
                            "parents": [
                                {
                                    "text": "TagName1",
                                    "link": "TAGN1"
                                },
                                {
                                    "text": "TagName2",
                                    "link": "TAGN2"
                                }
                            ]
                        }
                        """));
    }

    @Test
    public void testGetSubCategoriesParent() throws Exception {
        cslStubService.getLearningCatalogue().getLearningTags(learningTagsPagedResponse);
        mockMvc.perform(get("/learning/categories/TAGN1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json("""
                        {
                            "categories": [
                                {
                                    "title": "TagName2",
                                    "description": "TagName2 description",
                                    "url": "TAGN2"
                                },
                                {
                                    "title": "TagName5",
                                    "description": "TagName5 description",
                                    "url": "TAGN5"
                                }
                            ],
                            "title": "TagName1",
                            "description": "TagName1 description",
                            "parents": []
                        }
                        """));
    }

}
