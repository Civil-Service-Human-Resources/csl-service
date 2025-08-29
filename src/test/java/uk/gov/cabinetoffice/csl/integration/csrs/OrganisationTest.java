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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
                             "formattedName": "OrgName1 (OName1)",
                             "parentId": null,
                             "parent": null,
                             "domains": null,
                             "agencyToken": null
                         },
                         {
                             "id": 2,
                             "name": "OrgName2",
                             "code": "ON2",
                             "abbreviation": null,
                             "formattedName": "OrgName1 (OName1) | OrgName2",
                             "parentId": 1,
                             "parent": null,
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
                             "formattedName": "OrgName1 (OName1) | OrgName2 | OrgName3 (OName3)",
                             "parentId": 2,
                             "parent": null,
                             "domains": null,
                             "agencyToken": null
                         },
                         {
                             "id": 4,
                             "name": "OrgName4",
                             "code": "ON4",
                             "abbreviation": "OName4",
                             "formattedName": "OrgName1 (OName1) | OrgName2 | OrgName3 (OName3) | OrgName4 (OName4)",
                             "parentId": 3,
                             "parent": null,
                             "domains": null,
                             "agencyToken": null
                         },
                         {
                             "id": 5,
                             "name": "OrgName5",
                             "code": "ON5",
                             "abbreviation": "OName5",
                             "formattedName": "OrgName1 (OName1) | OrgName5 (OName5)",
                             "parentId": 1,
                             "parent": null,
                             "domains": null,
                             "agencyToken": null
                         },
                         {
                             "id": 6,
                             "name": "OrgName6",
                             "code": "ON6",
                             "abbreviation": "OName6",
                             "formattedName": "OrgName6 (OName6)",
                             "parentId": null,
                             "parent": null,
                             "domains": null,
                             "agencyToken": null
                         }
                    ]
                }
                """;

        mockMvc.perform(get("/organisations/full")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedOrganisations, true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testDeleteOrganisationalUnit() throws Exception {
        cslStubService.stubDeleteOrganisationalUnit(1L);
        mockMvc.perform(delete("/organisations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }
}
