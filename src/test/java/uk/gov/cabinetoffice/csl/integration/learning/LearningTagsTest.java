package uk.gov.cabinetoffice.csl.integration.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.data.ArrayJsonContentBuilder;
import uk.gov.cabinetoffice.csl.util.data.catalogue.JsonLearningTagBuilder;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LearningTagsTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CSLStubService cslStubService;

    private final String learningTagsPagedResponse = new ArrayJsonContentBuilder<JsonLearningTagBuilder>()
            .addElements(
                    JsonLearningTagBuilder.create(1L, null, null, "2025-01-01T10:00:00"),
                    JsonLearningTagBuilder.create(2L, 1L, "TagName1", "2025-01-01T10:00:00"),
                    JsonLearningTagBuilder.create(3L, 2L, "TagName2", "2025-01-01T10:00:00"),
                    JsonLearningTagBuilder.create(4L, null, null, "2025-01-01T10:00:00"),
                    JsonLearningTagBuilder.create(5L, 1L, "TagName1", "2025-01-01T10:00:00")
            ).getAsPaginatedAndBuild(0, 5, 1);

    @Test
    public void testGetLearningTagsTree() throws Exception {
        cslStubService.getLearningCatalogue().getLearningTags(learningTagsPagedResponse);

        mockMvc.perform(get("/learning-tags/overview-tree")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json("""
                        {
                            "content": [
                                {
                                    "name": "TagName1",
                                    "id": 1,
                                    "children": [
                                        {
                                            "name": "TagName2",
                                            "id": 2,
                                            "children": [
                                                {
                                                    "name": "TagName3",
                                                    "id": 3,
                                                    "children": []
                                                }
                                            ]
                                        },
                                        {
                                            "name": "TagName5",
                                            "id": 5,
                                            "children": []
                                        }
                                    ]
                                },
                                {
                                    "name": "TagName4",
                                    "id": 4,
                                    "children": []
                                }
                            ]
                        }
                        """, true));
    }

    @Test
    public void testGetLearningTagOverview() throws Exception {
        cslStubService.getLearningCatalogue().getLearningTags(learningTagsPagedResponse);
        mockMvc.perform(get("/learning-tags/2"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json("""
                        {
                            "id": 2,
                            "name": "TagName2",
                            "description": "TagName2 description",
                            "code": "TAGN2",
                            "urlSlug": "TAGN2",
                            "fullUrl": "TAGN1/TAGN2",
                            "parentId": 1,
                            "parentName": "TagName1",
                            "category": false,
                            "archived": false
                        }
                        """));
    }

    @Test
    public void testCreate() throws Exception {
        cslStubService.getLearningCatalogue().getLearningTags(learningTagsPagedResponse);
        cslStubService.getLearningCatalogue().createLearningTag("""
                {
                    "name" : "New tag 01",
                    "code" : "NEW_TAG",
                    "description" : null,
                    "parentId" : null,
                    "urlSlug" : "new-tag-01",
                    "archived" : false,
                    "category" : true
                }""", """
                {
                    "id": 1,
                    "code": "NEW_TAG",
                    "urlSlug": "new-tag-01",
                    "name": "New Tag",
                    "category" : true
                }
                """);
        mockMvc.perform(post("/learning-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "NEW_TAG",
                                  "name": "New tag 01",
                                  "parentId": null,
                                  "category" : true
                                }
                                """))
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "name": "New Tag",
                            "description": null,
                            "code": "NEW_TAG",
                            "urlSlug": "new-tag-01",
                            "fullUrl": "new-tag-01",
                            "parentId": null,
                            "parentName": null,
                            "category": true,
                            "archived": false
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testCreateGeneratedUrlSlugTooLong() throws Exception {
        mockMvc.perform(post("/learning-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "NEW_TAG",
                                  "name": "&&&&&&&&&&&&&&&&&",
                                  "parentId": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Auto-generated URL slug was greater than the max length of 50. Generated URL slug was andandandandandandandandandandandandandandandandand"));
    }

    @Test
    public void testCreateWithParent() throws Exception {
        cslStubService.getLearningCatalogue().getLearningTags(learningTagsPagedResponse);
        cslStubService.getLearningCatalogue().createLearningTag("""
                {
                    "name" : "New tag 01",
                    "code" : "NEW_TAG",
                    "description" : null,
                    "parentId" : 1,
                    "urlSlug" : "new-tag-01",
                    "archived" : false,
                    "category" : false
                }""", """
                {
                    "id": 7,
                    "code": "NEW_TAG",
                    "urlSlug": "new-tag-01",
                    "name": "New Tag"
                }
                """);
        mockMvc.perform(post("/learning-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "NEW_TAG",
                                  "name": "New tag 01",
                                  "parentId": 1
                                }
                                """))
                .andExpect(content().json("""
                        {
                            "id": 7,
                            "name": "New Tag",
                            "description": null,
                            "code": "NEW_TAG",
                            "urlSlug": "new-tag-01",
                            "fullUrl": "TAGN1/new-tag-01",
                            "parentId": 1,
                            "parentName": "TagName1",
                            "category": false,
                            "archived": false
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testUpdateLearningTag() throws Exception {
        cslStubService.getLearningCatalogue().getLearningTags(learningTagsPagedResponse);
        cslStubService.getLearningCatalogue().updateLearningTag(2, """
                {
                  "code": "TAGN2",
                  "name": "TagName2 edit",
                  "description": "TAGN2 new description",
                  "parentId": 5
                }""", """
                {
                  "code": "TAGN2",
                  "name": "TagName2 edit",
                  "description": "TAGN2 new description"
                }
                """);
        mockMvc.perform(put("/learning-tags/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "TAGN2",
                                  "name": "TagName2 edit",
                                  "description": "TAGN2 new description",
                                  "parentId": 5
                                }
                                """))
                .andExpect(content().json("""
                        {
                          "id": 2,
                          "name": "TagName2 edit",
                          "description": "TAGN2 new description",
                          "code": "TAGN2",
                          "urlSlug": "tagname2-edit",
                          "fullUrl": "TAGN1/TAGN5/tagname2-edit",
                          "parentId": 5,
                          "parentName": "TagName5",
                          "category": false,
                          "archived": false
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testFormattedList() throws Exception {
        cslStubService.getLearningCatalogue().getLearningTags(learningTagsPagedResponse);
        mockMvc.perform(get("/learning-tags/formatted_list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "names": [
                                {
                                    "id": 1,
                                    "name": "TagName1",
                                    "code": "TAGN1"
                                },
                                {
                                    "id": 2,
                                    "name": "TagName1 | TagName2",
                                    "code": "TAGN2"
                                },
                                {
                                    "id": 3,
                                    "name": "TagName1 | TagName2 | TagName3",
                                    "code": "TAGN3"
                                },
                                {
                                    "id": 5,
                                    "name": "TagName1 | TagName5",
                                    "code": "TAGN5"
                                },
                                {
                                    "id": 4,
                                    "name": "TagName4",
                                    "code": "TAGN4"
                                }
                            ]
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
    }

}
