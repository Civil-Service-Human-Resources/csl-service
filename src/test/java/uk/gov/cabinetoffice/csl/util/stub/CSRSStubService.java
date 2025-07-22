package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.csrs.record.OrganisationalUnitsPagedResponse;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class CSRSStubService {

    private final CslTestUtil utils;

    public CSRSStubService(CslTestUtil utils) {
        this.utils = utils;
    }

    public StubMapping getCivilServant(String uid, CivilServant response) {
        return getCivilServant(uid, utils.toJson(response));
    }

    public StubMapping getCivilServant(String uid, String response) {
        return stubFor(
                WireMock.get(urlPathEqualTo("/csrs/civilServants/resource/" + uid + "/profile"))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping patchCivilServant(String expectedInput) {
        return stubFor(
                WireMock.patch(urlPathEqualTo("/csrs/civilServants/me"))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json"))
        );
    }

    public StubMapping getAreasOfWork(String response) {
        return stubFor(
                WireMock.get(urlPathEqualTo("/csrs/professions/tree"))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping getGrades(String response) {
        return stubFor(
                WireMock.get(urlPathEqualTo("/csrs/grades"))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping getOrganisations(OrganisationalUnitsPagedResponse response) {
        return getOrganisations(utils.toJson(response));
    }

    public StubMapping getOrganisations(String response) {
        return stubFor(
                WireMock.get(urlPathEqualTo("/csrs/v2/organisationalUnits"))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }
}
