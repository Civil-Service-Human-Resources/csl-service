package uk.gov.cabinetoffice.csl.integration.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.data.ArrayJsonContentBuilder;
import uk.gov.cabinetoffice.csl.util.data.catalogue.JsonLearningTagBuilder;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                            "categoryTag": false,
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
                    "category" : false
                }""", """
                {
                    "id": 1,
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
                                  "parentId": null
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
                            "categoryTag": false,
                            "archived": false
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
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
                            "categoryTag": false,
                            "archived": false
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
    }

}
