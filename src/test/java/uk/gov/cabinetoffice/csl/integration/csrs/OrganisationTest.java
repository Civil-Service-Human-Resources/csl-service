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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                             "name": "OrgName1"
                         },
                         {
                             "id": 2,
                             "name": "OrgName1 | OrgName2"
                         },
                         {
                             "id": 3,
                             "name": "OrgName1 | OrgName2 | OrgName3"
                         },
                         {
                             "id": 4,
                             "name": "OrgName1 | OrgName2 | OrgName3 | OrgName4"
                         },
                         {
                             "id": 5,
                             "name": "OrgName1 | OrgName5"
                         },
                         {
                             "id": 6,
                             "name": "OrgName6"
                         }
                    ]
                }
                """;

        mockMvc.perform(get("/organisations/formatted_list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> utils.toJson(expectedFormattedOrganisations))
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
                             "name": "OrgName1"
                             "code": "ON1",
                             "abbreviation": "ON1",
                             "formattedName": "OrgName1",
                             "href": "https://hostname/organisationalUnits/1",
                             "parentId": null,
                             "parent": null,
                             "children": null,
                             "domains":
                             [],
                             "agencyToken": null
                         },
                         {
                             "id": 2,
                             "name": "OrgName2"
                             "code": "ON2",
                             "abbreviation": "ON2",
                             "formattedName": "OrgName1 | OrgName2",
                             "href": "https://hostname/organisationalUnits/2",
                             "parentId": 1,
                             "parent": null,
                             "children": null,
                             "domains":
                             [],
                             "agencyToken": null
                         },
                         {
                             "id": 3,
                             "name": "OrgName3"
                             "code": "ON3",
                             "abbreviation": "ON3",
                             "formattedName": "OrgName1 | OrgName2 | OrgName3",
                             "href": "https://hostname/organisationalUnits/3",
                             "parentId": 2,
                             "parent": null,
                             "children": null,
                             "domains": [],
                             "agencyToken": null
                         },
                         {
                             "id": 4,
                             "name": "OrgName4"
                             "code": "ON4",
                             "abbreviation": "ON4",
                             "formattedName": "OrgName1 | OrgName2 | OrgName3 | OrgName4",
                             "href": "https://hostname/organisationalUnits/4",
                             "parentId": 3,
                             "parent": null,
                             "children": null,
                             "domains": [],
                             "agencyToken": null
                         },
                         {
                             "id": 5,
                             "name": "OrgName5"
                             "code": "ON5",
                             "abbreviation": "ON5",
                             "formattedName": "OrgName1 | OrgName5",
                             "href": "https://hostname/organisationalUnits/5",
                             "parentId": 1,
                             "parent": null,
                             "children": null,
                             "domains": [],
                             "agencyToken": null
                         },
                         {
                             "id": 6,
                             "name": "OrgName6",
                             "code": "ON6",
                             "abbreviation": "ON6",
                             "formattedName": "OrgName6",
                             "href": "https://hostname/organisationalUnits/6",
                             "parentId": null,
                             "parent": null,
                             "children": null,
                             "domains": [],
                             "agencyToken": null
                         }
                    ]
                }
                """;

        mockMvc.perform(get("/organisations/full")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> utils.toJson(expectedOrganisations))
                .andExpect(status().is2xxSuccessful());
    }
}
