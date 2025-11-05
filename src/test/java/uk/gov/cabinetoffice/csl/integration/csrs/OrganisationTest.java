package uk.gov.cabinetoffice.csl.integration.csrs;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.Domain;
import uk.gov.cabinetoffice.csl.domain.csrs.record.OrganisationalUnitsPagedResponse;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class OrganisationTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CSLStubService cslStubService;

    private OrganisationalUnitsPagedResponse organisationalUnitsPagedResponse;

    @PostConstruct
    public void setupData() {
        organisationalUnitsPagedResponse = testDataService.generateOrganisationalUnitsPagedResponse();
    }

    @Test
    public void testGetOrganisationalUnitOverview() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        mockMvc.perform(get("/organisations/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "id": 2,
                          "name": "OrgName2",
                          "code": "ON2",
                          "abbreviation": null,
                          "parentId": 1,
                          "parentName": "OrgName1",
                          "domains": [
                            {
                              "id": 1,
                              "domain": "domain2.com",
                              "createdTimestamp": "2025-01-01T10:00:00"
                            }
                          ],
                          "agencyToken": null
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testCreateOrganisationalUnit() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        cslStubService.getCsrsStubService().createOrganisation("""
                {
                    "code": "NEW_OU",
                    "name": "New Organisational Unit",
                    "abbreviation": "NOU",
                    "parentId": null
                }""", """
                {
                    "ID": 1,
                    "code": "NEW_OU",
                    "name": "New Organisational Unit",
                    "abbreviation": "NOU",
                    "parentId": null
                }
                """);
        mockMvc.perform(post("/organisations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "NEW_OU",
                                  "name": "New Organisational Unit",
                                  "abbreviation": "NOU",
                                  "parentId": null
                                }
                                """))
                .andExpect(content().json("""
                        {
                          "id": null,
                          "name": "New Organisational Unit",
                          "code": "NEW_OU",
                          "abbreviation": "NOU",
                          "parentId": null,
                          "parentName": null,
                          "domains": [],
                          "agencyToken": null
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testCreateOrganisationalUnitWithParent() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        cslStubService.getCsrsStubService().createOrganisation("""
                {
                    "code": "NEW_OU",
                    "name": "New Organisational Unit",
                    "abbreviation": "NOU",
                    "parentId": 1
                }""", """
                {
                    "id": 10,
                    "code": "NEW_OU",
                    "name": "New Organisational Unit",
                    "abbreviation": "NOU",
                    "parentId": 1
                }
                """);
        mockMvc.perform(post("/organisations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "code": "NEW_OU",
                                    "name": "New Organisational Unit",
                                    "abbreviation": "NOU",
                                    "parentId": 1
                                }
                                """))
                .andExpect(content().json("""
                          {
                            "id":10,
                            "name":"New Organisational Unit",
                            "code":"NEW_OU",
                            "abbreviation":"NOU",
                            "parentId":1,
                            "parentName":"OrgName1",
                            "domains":[],
                            "agencyToken":null
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testUpdateOrganisationalUnitParentToChild() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        mockMvc.perform(put("/organisations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "ON2",
                                  "name": "OrgName2 edit",
                                  "abbreviation": "ON2E",
                                  "parentId": 2
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateOrganisationalUnitParentToSelf() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        mockMvc.perform(put("/organisations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "ON2",
                                  "name": "OrgName2 edit",
                                  "abbreviation": "ON2E",
                                  "parentId": 1
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateOrganisationalUnit() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        cslStubService.getCsrsStubService().updateOrganisation(2, """
                {
                  "code": "ON2",
                  "name": "OrgName2 edit",
                  "abbreviation": "ON2E",
                  "parentId": 6
                }""", """
                {
                  "code": "ON2",
                  "name": "OrgName2 edit",
                  "abbreviation": "ON2E",
                  "parentId": 6
                }
                """);
        mockMvc.perform(put("/organisations/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "ON2",
                                  "name": "OrgName2 edit",
                                  "abbreviation": "ON2E",
                                  "parentId": 6
                                }
                                """))
                .andExpect(content().json("""
                        {
                          "id":2,
                          "code": "ON2",
                          "name": "OrgName2 edit",
                          "abbreviation": "ON2E",
                          "parentName":"OrgName6",
                          "parentId": 6,
                          "domains":[{"id":1,"domain":"domain2.com","createdTimestamp":"2025-01-01T10:00:00"}],
                          "agencyToken":null
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testUnlinkParent() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        cslStubService.getCsrsStubService().updateOrganisation(2, """
                {
                  "code": "ON2",
                  "name": "OrgName2",
                  "abbreviation": null,
                  "parentId": null
                }""", """
                {
                  "code": "ON2",
                  "name": "OrgName2",
                  "abbreviation": null,
                  "parentId": null
                }
                """);
        mockMvc.perform(put("/organisations/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "ON2",
                                  "name": "OrgName2",
                                  "abbreviation": null,
                                  "parentId": null
                                }
                                """))
                .andExpect(content().json("""
                        {
                          "id":2,
                          "name":"OrgName2",
                          "code":"ON2",
                          "abbreviation":null,
                          "parentId":null,
                          "parentName":null,
                          "domains":[
                            {
                              "id":1,
                              "domain":"domain2.com",
                              "createdTimestamp":"2025-01-01T10:00:00"
                            }
                          ],
                          "agencyToken":null
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testGetOrganisationalUnitsTree() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        String expectedFormattedOrganisations = """
                {
                  "organisationalUnits": [
                    {
                      "name": "OrgName1",
                      "id": 1,
                      "children": [
                        {
                          "name": "OrgName2",
                          "id": 2,
                          "children": [
                            {
                              "name": "OrgName3",
                              "id": 3,
                              "children": [{ "name": "OrgName4", "id": 4, "children": [] }]
                            }
                          ]
                        },
                        { "name": "OrgName5", "id": 5, "children": [] }
                      ]
                    },
                    { "name": "OrgName6", "id": 6, "children": [] },
                    {
                      "name": "OrgName7",
                      "id": 7,
                      "children": [
                        { "name": "OrgName8", "id": 8, "children": [] },
                        { "name": "OrgName9", "id": 9, "children": [] }
                      ]
                    }
                  ]
                }
                """;

        mockMvc.perform(get("/organisations/overview-tree")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedFormattedOrganisations, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testGetOrganisationOverviews() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        String expectedFormattedOrganisations = """
                {
                  "organisationalUnits": [
                    {
                      "id": 1,
                      "name": "OrgName1",
                      "code": "ON1",
                      "abbreviation": "OName1",
                      "parentId": null,
                      "parentName": null,
                      "domains": [],
                      "agencyToken": null
                    },
                    {
                      "id": 2,
                      "name": "OrgName2",
                      "code": "ON2",
                      "abbreviation": null,
                      "parentId": 1,
                      "parentName": "OrgName1",
                      "domains": [
                        {
                          "id": 1,
                          "domain": "domain2.com",
                          "createdTimestamp": "2025-01-01T10:00:00"
                        }
                      ],
                      "agencyToken": null
                    },
                    {
                      "id": 3,
                      "name": "OrgName3",
                      "code": "ON3",
                      "abbreviation": "OName3",
                      "parentId": 2,
                      "parentName": "OrgName2",
                      "domains": [],
                      "agencyToken": null
                    }
                  ]
                }
                
                """;
        mockMvc.perform(get("/organisations")
                        .param("organisationId", "3")
                        .param("includeParents", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedFormattedOrganisations, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testGetOrganisationOverviewsNotParent() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        String expectedFormattedOrganisations = """
                {
                  "organisationalUnits": [
                    {
                      "id": 1,
                      "name": "OrgName1",
                      "code": "ON1",
                      "abbreviation": "OName1",
                      "parentId": null,
                      "parentName": null,
                      "domains": [],
                      "agencyToken": null
                    }
                  ]
                }
                
                """;
        mockMvc.perform(get("/organisations")
                        .param("organisationId", "1")
                        .param("includeParents", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedFormattedOrganisations, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testFormattedOrganisationsList() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        String expectedFormattedOrganisations = """
                {
                    "formattedOrganisationalUnitNames": [
                         {
                             "id": 1,
                             "name": "OrgName1 (OName1)",
                             "code": "ON1",
                             "abbreviation": "OName1"
                         },
                         {
                             "id": 2,
                             "name": "OrgName1 (OName1) | OrgName2",
                             "code": "ON2",
                             "abbreviation": null
                         },
                         {
                             "id": 3,
                             "name": "OrgName1 (OName1) | OrgName2 | OrgName3 (OName3)",
                             "code": "ON3",
                             "abbreviation":  "OName3"
                         },
                         {
                             "id": 4,
                             "name": "OrgName1 (OName1) | OrgName2 | OrgName3 (OName3) | OrgName4 (OName4)",
                             "code": "ON4",
                             "abbreviation":  "OName4"
                         },
                         {
                             "id": 5,
                             "name": "OrgName1 (OName1) | OrgName5 (OName5)",
                             "code": "ON5",
                             "abbreviation":  "OName5"
                         },
                         {
                             "id": 6,
                             "name": "OrgName6 (OName6)",
                             "code": "ON6",
                             "abbreviation":  "OName6"
                         },
                         {
                          "id": 7,
                          "name": "OrgName7 (OName7)",
                          "code": "ON7",
                          "abbreviation": "OName7"
                        },
                        {
                          "id": 8,
                          "name": "OrgName7 (OName7) | OrgName8 (OName8)",
                          "code": "ON8",
                          "abbreviation": "OName8"
                        },
                        {
                          "id": 9,
                          "name": "OrgName7 (OName7) | OrgName9 (OName9)",
                          "code": "ON9",
                          "abbreviation": "OName9"
                        }
                    ]
                }
                """;

        mockMvc.perform(get("/organisations/formatted_list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedFormattedOrganisations, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testFormattedOrganisationsListForAgency() throws Exception {
        OrganisationalUnitsPagedResponse organisationalUnits = testDataService.generateOrganisationalUnitsPagedResponse();
        cslStubService.stubGetOrganisations(organisationalUnits);
        String expectedFormattedOrganisations = """
                {
                    "formattedOrganisationalUnitNames": [
                        {
                          "id": 7,
                          "name": "OrgName7 (OName7)",
                          "code": "ON7",
                          "abbreviation": "OName7"
                        },
                        {
                          "id": 8,
                          "name": "OrgName7 (OName7) | OrgName8 (OName8)",
                          "code": "ON8",
                          "abbreviation": "OName8"
                        }
                    ]
                }
                """;

        mockMvc.perform(get("/organisations/formatted_list")
                        .param("domain", "agency.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedFormattedOrganisations, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testFormattedOrganisationsListWithFilter() throws Exception {
        OrganisationalUnitsPagedResponse organisationalUnits = testDataService.generateOrganisationalUnitsPagedResponse();
        organisationalUnits.getContent().get(3).setDomains(List.of(new Domain(1L, "domain2.com", LocalDateTime.of(2025, 1, 1, 10, 0, 0))));
        cslStubService.stubGetOrganisations(organisationalUnits);
        String expectedFormattedOrganisations = """
                {
                    "formattedOrganisationalUnitNames": [
                        {
                          "id": 1,
                          "name": "OrgName1 (OName1)",
                          "code": "ON1",
                          "abbreviation": "OName1"
                        },
                        {
                          "id": 2,
                          "name": "OrgName1 (OName1) | OrgName2",
                          "code": "ON2",
                          "abbreviation": null
                        },
                        {
                          "id": 3,
                          "name": "OrgName1 (OName1) | OrgName2 | OrgName3 (OName3)",
                          "code": "ON3",
                          "abbreviation": "OName3"
                        },
                        {
                          "id": 4,
                          "name": "OrgName1 (OName1) | OrgName2 | OrgName3 (OName3) | OrgName4 (OName4)",
                          "code": "ON4",
                          "abbreviation": "OName4"
                        },
                        {
                          "id": 6,
                          "name": "OrgName6 (OName6)",
                          "code": "ON6",
                          "abbreviation": "OName6"
                        }
                    ]
                }
                """;

        mockMvc.perform(get("/organisations/formatted_list")
                        .param("domain", "domain2.com")
                        .param("tierOne", "true")
                        .param("organisationId", "6")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedFormattedOrganisations, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testFullOrganisationsList() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        String expectedOrganisations = """
                {
                  "organisationalUnits": [
                    {
                      "id": 1,
                      "name": "OrgName1",
                      "code": "ON1",
                      "abbreviation": "OName1",
                      "parentId": null,
                      "parent": null,
                      "domains": [],
                      "agencyToken": null,
                      "formattedName": "OrgName1 (OName1)",
                      "parentName": null
                    },
                    {
                      "id": 2,
                      "name": "OrgName2",
                      "code": "ON2",
                      "abbreviation": null,
                      "parentId": 1,
                      "parent":  null,
                      "domains": [
                        {
                          "id": 1,
                          "domain": "domain2.com",
                          "createdTimestamp": "2025-01-01T10:00:00"
                        }
                      ],
                      "agencyToken": null,
                      "formattedName": "OrgName1 (OName1) | OrgName2",
                      "parentName": "OrgName1"
                    },
                    {
                      "id": 3,
                      "name": "OrgName3",
                      "code": "ON3",
                      "abbreviation": "OName3",
                      "parentId": 2,
                      "parent":  null,
                      "domains": [],
                      "agencyToken": null,
                      "formattedName": "OrgName1 (OName1) | OrgName2 | OrgName3 (OName3)",
                      "parentName": "OrgName2"
                    },
                    {
                      "id": 4,
                      "name": "OrgName4",
                      "code": "ON4",
                      "abbreviation": "OName4",
                      "parentId": 3,
                      "parent":  null,
                      "domains": [],
                      "agencyToken": null,
                      "formattedName": "OrgName1 (OName1) | OrgName2 | OrgName3 (OName3) | OrgName4 (OName4)",
                      "parentName": "OrgName3"
                    },
                    {
                      "id": 5,
                      "name": "OrgName5",
                      "code": "ON5",
                      "abbreviation": "OName5",
                      "parentId": 1,
                      "parent":  null,
                      "domains": [],
                      "agencyToken": null,
                      "formattedName": "OrgName1 (OName1) | OrgName5 (OName5)",
                      "parentName": "OrgName1"
                    },
                    {
                      "id": 6,
                      "name": "OrgName6",
                      "code": "ON6",
                      "abbreviation": "OName6",
                      "parentId": null,
                      "parent":  null,
                      "domains": [],
                      "agencyToken": null,
                      "formattedName": "OrgName6 (OName6)",
                      "parentName": null
                    },
                    {
                      "id": 7,
                      "name": "OrgName7",
                      "code": "ON7",
                      "abbreviation": "OName7",
                      "parentId": null,
                      "parent":  null,
                      "domains": [],
                      "agencyToken": {
                        "id": 1,
                        "token": "token",
                        "uid": "uid1",
                        "capacity": 30,
                        "agencyDomains": [{ "id": 1, "domain": "agency.com" }]
                      },
                      "formattedName": "OrgName7 (OName7)",
                      "parentName": null
                    },
                    {
                      "id": 8,
                      "name": "OrgName8",
                      "code": "ON8",
                      "abbreviation": "OName8",
                      "parentId": 7,
                      "parent":  null,
                      "domains": [],
                      "agencyToken": null,
                      "formattedName": "OrgName7 (OName7) | OrgName8 (OName8)",
                      "parentName": "OrgName7"
                    },
                    {
                      "id": 9,
                      "name": "OrgName9",
                      "code": "ON9",
                      "abbreviation": "OName9",
                      "parentId": 7,
                      "parent":  null,
                      "domains": [],
                      "agencyToken": {
                        "id": 2,
                        "token": "token",
                        "uid": "uid2",
                        "capacity": 1,
                        "agencyDomains": [{ "id": 2, "domain": "agency2.com" }]
                      },
                      "formattedName": "OrgName7 (OName7) | OrgName9 (OName9)",
                      "parentName": "OrgName7"
                    }
                  ]
                }
                """;

        mockMvc.perform(get("/organisations/full")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedOrganisations, true))
                .andExpect(status().is2xxSuccessful());
    }
}
