package uk.gov.cabinetoffice.csl.integration.csrs;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.record.OrganisationalUnitsPagedResponse;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class AgencyTokenTest extends IntegrationTestBase {

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
    public void testAddAgencyTokenToOrganisations() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        cslStubService.getCsrsStubService().addToken(1, """
                {
                    "token": "abc",
                    "capacity": 10,
                    "agencyDomains": [
                        {
                            "domain": "domain.com"
                        },
                        {
                            "domain": "domain2.com"
                        }
                    ]
                }
                """, """
                {
                    "id": 10,
                    "token": "abc",
                    "uid": "UID",
                    "capacity": 10,
                    "agencyDomains": [
                        {
                            "id": 1,
                            "domain": "domain.com",
                            "createdTimestamp": "2025-01-01T10:00:00"
                        },
                        {
                            "id": 2,
                            "domain": "domain2.com",
                            "createdTimestamp": "2025-01-01T10:00:00"
                        }
                    ]
                }
                """);
        mockMvc.perform(post("/organisations/1/agency-token")
                        .content("""
                                {
                                    "domain": [
                                        "domain.com",
                                        "domain2.com"
                                    ],
                                    "capacity": 10,
                                    "token": "abc"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "id": 1,
                          "name": "OrgName1",
                          "code": "ON1",
                          "abbreviation": "OName1",
                          "parentId": null,
                          "parentName": null,
                          "domains": [],
                          "agencyToken": {
                            "id": 10,
                            "uid": "UID",
                            "token": "abc",
                            "capacity": 10,
                            "capacityUsed": 0,
                            "agencyDomains": [
                              { "id": 1, "domain": "domain.com" },
                              { "id": 2, "domain": "domain2.com" }
                            ]
                          }
                        }
                        """, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testUpdateAgencyToken() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        cslStubService.getIdentityAPIServiceStubService().getAgencyTokenSpacesUsed("uid1", 30);
        cslStubService.getCsrsStubService().updateToken(7, """
                {
                    "token": "abc",
                    "capacity": 30,
                    "agencyDomains": [
                        {
                            "domain": "domain.com"
                        },
                        {
                            "domain": "domain2.com"
                        }
                    ]
                }
                """, """
                {
                    "id": 1,
                    "token": "abc",
                    "uid": "uid1",
                    "capacity": 30,
                    "agencyDomains": [
                        {
                            "id": 1,
                            "domain": "domain.com",
                            "createdTimestamp": "2025-01-01T10:00:00"
                        },
                        {
                            "id": 2,
                            "domain": "domain2.com",
                            "createdTimestamp": "2025-01-01T10:00:00"
                        }
                    ]
                }
                """);
        mockMvc.perform(put("/organisations/7/agency-token")
                        .content("""
                                {
                                    "domain": [
                                        "domain.com",
                                        "domain2.com"
                                    ],
                                    "capacity": 30,
                                    "token": "abc"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "id": 7,
                          "name": "OrgName7",
                          "code": "ON7",
                          "abbreviation": "OName7",
                          "parentId": null,
                          "parentName": null,
                          "domains": [],
                          "agencyToken": {
                            "id": 1,
                            "uid": "uid1",
                            "token": "abc",
                            "capacity": 30,
                            "capacityUsed": 30,
                            "agencyDomains": [
                              { "id": 1, "domain": "domain.com" },
                              { "id": 2, "domain": "domain2.com" }
                            ]
                          }
                        }""", true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testUpdateAgencyTokenInvalidCapacity() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        cslStubService.getIdentityAPIServiceStubService().getAgencyTokenSpacesUsed("uid1", 20);
        mockMvc.perform(put("/organisations/7/agency-token")
                        .content("""
                                {
                                    "agencyDomains": [
                                        "domain.com",
                                        "domain2.com"
                                    ],
                                    "capacity": 10,
                                    "token": "abc"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testRemoveAgencyTokenFromOrganisations() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        cslStubService.getCsrsStubService().deleteToken(7);
        mockMvc.perform(delete("/organisations/7/agency-token"))
                .andExpect(status().is2xxSuccessful());
    }

}
