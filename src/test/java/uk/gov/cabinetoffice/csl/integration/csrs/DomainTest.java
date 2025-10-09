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

import java.util.List;

import static java.time.LocalDateTime.now;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class DomainTest extends IntegrationTestBase {

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
    public void testAddDomainToOrganisations() throws Exception {
        cslStubService.stubGetOrganisations(organisationalUnitsPagedResponse);
        cslStubService.getCsrsStubService().addDomain(1, """
                {"domain": "abc.com"}
                """, """
                {
                    "domain": {
                        "id": 1,
                        "domain": "abc.com",
                        "createdTimestamp": "2025-01-01T10:00:00"
                    },
                    "updatedChildOrganisationIds": [2,3,4]
                }
                """);
        mockMvc.perform(post("/organisations/1/domains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"domain": "abc.com"}
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.updatedChildIds[0]").value(2))
                .andExpect(jsonPath("$.updatedChildIds[1]").value(3))
                .andExpect(jsonPath("$.updatedChildIds[2]").value(4))
                .andExpect(jsonPath("$.domain.id").value(1))
                .andExpect(jsonPath("$.domain.domain").value("abc.com"))
                .andExpect(jsonPath("$.domain.createdTimestamp").value("2025-01-01T10:00:00"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testRemoveDomainFromOrganisations() throws Exception {
        Domain d = new Domain(1L, "abc.com", now());
        OrganisationalUnitsPagedResponse orgResponse = testDataService.generateOrganisationalUnitsPagedResponse();
        orgResponse.getContent().get(0).setDomains(List.of(d));
        orgResponse.getContent().get(1).setDomains(List.of(d));
        orgResponse.getContent().get(2).setDomains(List.of(d));
        orgResponse.getContent().get(3).setDomains(List.of(d));
        cslStubService.stubGetOrganisations(orgResponse);
        cslStubService.getCsrsStubService().deleteDomain(1, 1, true, """
                {
                    "updatedChildOrganisationIds": [2,3,4]
                }
                """);
        mockMvc.perform(delete("/organisations/1/domains/1")
                        .param("includeSubOrganisations", "true"))
                .andExpect(jsonPath("$.updatedChildIds[0]").value(2))
                .andExpect(jsonPath("$.updatedChildIds[1]").value(3))
                .andExpect(jsonPath("$.updatedChildIds[2]").value(4))
                .andExpect(status().is2xxSuccessful());
    }

}
