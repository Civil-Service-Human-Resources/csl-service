package uk.gov.cabinetoffice.csl.integration.learning;

import org.junit.jupiter.api.BeforeEach;
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
                    JsonLearningTagBuilder.create(1L, null, null, "2025-01-01T10:00:00")
                            .courseCount(2).linkCount(1),
                    JsonLearningTagBuilder.create(2L, 1L, "TagName1", "2025-01-01T10:00:00"),
                    JsonLearningTagBuilder.create(3L, 2L, "TagName2", "2025-01-01T10:00:00"),
                    JsonLearningTagBuilder.create(4L, null, null, "2025-01-01T10:00:00"),
                    JsonLearningTagBuilder.create(5L, 1L, "TagName1", "2025-01-01T10:00:00"),
                    JsonLearningTagBuilder.create(6L, null, null, "2025-01-01T10:00:00").isArchived()
            ).getAsPaginatedAndBuild(0, 5, 1);

    @BeforeEach
    void before() {
        cslStubService.getLearningCatalogue().getLearningTags(learningTagsPagedResponse);
    }

    @Test
    public void testGetLearningTagsTree() throws Exception {

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
                                                    "children": [],
                                                    "archived": false
                                                }
                                            ],
                                            "archived": false
                                        },
                                        {
                                            "name": "TagName5",
                                            "id": 5,
                                            "children": [],
                                            "archived": false
                                        }
                                    ],
                                    "archived": false
                                },
                                {
                                    "name": "TagName4",
                                    "id": 4,
                                    "children": [],
                                    "archived": false
                                },
                                {
                                    "name": "TagName6",
                                    "id": 6,
                                    "children": [],
                                    "archived": true
                                }
                            ]
                        }
                        """, true));
    }

    @Test
    public void testGetLearningTagOverview() throws Exception {
        mockMvc.perform(get("/learning-tags/2"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json("""
                        {
                            "id": 2,
                            "name": "TagName2",
                            "description": "TagName2 description",
                            "code": "TAGN2",
                            "urlSlug": "TAGN2",
                            "parentId": 1,
                            "parentName": "TagName1",
                            "category": false,
                            "archived": false
                        }
                        """));
    }

    @Test
    public void testCreate() throws Exception {
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
        cslStubService.getLearningCatalogue().updateLearningTag(2, """
                {
                  "code": "TAGN2",
                  "name": "TagName2 edit",
                  "description": "TAGN2 new description",
                  "parentId": 5,
                  "category": true
                }""", """
                {
                  "code": "TAGN2",
                  "name": "TagName2 edit",
                  "description": "TAGN2 new description",
                  "category": true
                }
                """);
        mockMvc.perform(put("/learning-tags/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "TAGN2",
                                  "name": "TagName2 edit",
                                  "description": "TAGN2 new description",
                                  "parentId": 5,
                                  "category": true
                                }
                                """))
                .andExpect(content().json("""
                        {
                          "id": 2,
                          "name": "TagName2 edit",
                          "description": "TAGN2 new description",
                          "code": "TAGN2",
                          "urlSlug": "tagname2-edit",
                          "parentId": 5,
                          "parentName": "TagName5",
                          "category": true,
                          "archived": false
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testUpdateLearningTagNullSlug() throws Exception {
        cslStubService.getLearningCatalogue().updateLearningTag(2, """
                {
                  "code": "TAGN2",
                  "name": "TagName2 edit",
                  "description": "TAGN2 new description",
                  "category": true,
                  "urlSlug": "tagname2-edit"
                }""", """
                {
                  "code": "TAGN2",
                  "name": "TagName2 edit",
                  "description": "TAGN2 new description",
                  "category": true,
                  "urlSlug": "tagname2-edit"
                }
                """);
        mockMvc.perform(put("/learning-tags/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "TAGN2",
                                  "name": "TagName2 edit",
                                  "description": "TAGN2 new description",
                                  "parentId": 1,
                                  "category": true,
                                  "urlSlug": null
                                }
                                """))
                .andExpect(content().json("""
                        {
                          "id": 2,
                          "name": "TagName2 edit",
                          "description": "TAGN2 new description",
                          "code": "TAGN2",
                          "urlSlug": "tagname2-edit",
                          "parentId": 1,
                          "parentName": "TagName1",
                          "category": true,
                          "archived": false
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testFormattedList() throws Exception {
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
                                },
                                {
                                    "id": 6,
                                    "name": "TagName6",
                                    "code": "TAGN6"
                                }
                            ]
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testArchive() throws Exception {
        String expectedStateUpdate = """
                {
                    "state": "ARCHIVE",
                    "ids": [1, 2, 3, 5]
                }
                """;
        String response = """
                {
                    "successfulUpdates": [1,2,3],
                    "failedUpdates": []
                }
                """;
        cslStubService.getLearningCatalogue().updateLearningTagState(expectedStateUpdate, response);
        mockMvc.perform(put("/learning-tags/1/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "state": "ARCHIVE"
                                }
                                """))
                .andExpect(content().json("""
                          {
                            "id":1,
                            "name":"TagName1",
                            "description":"TagName1 description",
                            "code":"TAGN1",
                            "urlSlug":"TAGN1",
                            "parentId":null,
                            "parentName":null,
                            "category":false,
                            "archived":true
                          }
                        """))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testGetCoursesForLearningTag() throws Exception {
        Long tagId = 1L;
        int page = 0;
        int size = 20;
        String response = """
                {
                  "results": [
                    {
                      "id": "course-id-1",
                      "title": "Course Title 1",
                      "status": "Published",
                      "shortDescription": "Short description for Course Title 1"
                    },
                    {
                      "id": "course-id-2",
                      "title": "Course Title 2",
                      "status": "Published",
                      "shortDescription": "Short description for Course Title 2"
                    }
                  ],
                  "page": 0,
                  "size": 20,
                  "totalResults": 2,
                  "totalElements": 2,
                  "totalPages": 1,
                  "numberOfElements": 2,
                  "last": true,
                  "first": true
                }
                """;
        cslStubService.getLearningCatalogue().getCoursesForLearningTag(tagId, page, size, response);

        mockMvc.perform(get("/learning-tags/{tagId}/courses", tagId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(response));
    }

    @Test
    public void testGetHyperlinksForLearningTag() throws Exception {
        Long tagId = 1L;
        int page = 0;
        int size = 20;
        String response = """
                {
                  "results": [
                    {
                      "id": 1,
                      "title": "BBC",
                      "description": "The BBC is a news website",
                      "href": "https://bbc.co.uk"
                    }
                  ],
                  "page": 0,
                  "size": 20,
                  "totalResults": 7,
                  "totalPages": 1,
                  "totalElements": 7,
                  "numberOfElements": 7,
                  "last": true,
                  "first": true
                }
                """;
        cslStubService.getLearningCatalogue().getHyperlinksForLearningTag(tagId, page, size, response);

        mockMvc.perform(get("/learning-tags/{tagId}/hyperlinks", tagId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(response));
    }

    @Test
    public void testGetHyperlinksForLearningTagDefaultPagination() throws Exception {
        Long tagId = 1L;
        int page = 0;
        int size = 20;
        String response = """
                {
                  "results": [
                    {
                      "id": 1,
                      "title": "BBC",
                      "description": "The BBC is a news website",
                      "href": "https://bbc.co.uk"
                    }
                  ],
                  "page": 0,
                  "size": 20,
                  "totalResults": 7,
                  "totalPages": 1,
                  "totalElements": 7,
                  "numberOfElements": 7,
                  "last": true,
                  "first": true
                }
                """;
        cslStubService.getLearningCatalogue().getHyperlinksForLearningTag(tagId, page, size, response);

        mockMvc.perform(get("/learning-tags/{tagId}/hyperlinks", tagId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(response));
    }

    @Test
    public void testDeleteHyperlinksFromLearningTag() throws Exception {
        Long tagId = 1L;
        String request = """
                {
                  "ids": ["1", "2"]
                }
                """;
        String response = """
                {
                  "successfulIds": ["1"],
                  "failedIds": ["2"]
                }
                """;
        cslStubService.getLearningCatalogue().deleteHyperlinksFromLearningTag(tagId, request, response);

        mockMvc.perform(delete("/learning-tags/{tagId}/hyperlinks", tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(content().json(response));
    }

    @Test
    public void testDeleteCoursesFromLearningTag() throws Exception {
        Long tagId = 1L;
        String request = """
                {
                  "ids": ["course-id-1", "course-id-2"]
                }
                """;
        String response = """
                {
                  "successfulIds": ["course-id-1"],
                  "failedIds": ["course-id-2"]
                }
                """;
        cslStubService.getLearningCatalogue().deleteCoursesFromLearningTag(tagId, request, response);

        mockMvc.perform(delete("/learning-tags/{tagId}/courses", tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(content().json(response));
    }

    @Test
    public void testAssignCoursesToLearningTags() throws Exception {
        String request = """
                {
                    "learningTagIds": [1, 2],
                    "courseIds": ["course-id-1", "course-id-2"]
                }
                """;
        String response = """
                {
                    "successfulIds": [
                      {"learningTagId": 1, "successfulIds":  ["course-id-1", "course-id-2"]},
                      {"learningTagId": 2, "successfulIds":  ["course-id-1"]}
                    ],
                    "failedIds": []
                }
                """;
        cslStubService.getLearningCatalogue().assignCoursesToLearningTags(request, response);

        mockMvc.perform(post("/learning-tags/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(content().json(response));
    }
}
