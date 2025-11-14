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

    public StubMapping patchCivilServantOrganisation(String expectedInput) {
        return stubFor(
                WireMock.patch(urlPathEqualTo("/csrs/civilServants/me/organisationalUnit"))
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

    public StubMapping deleteOrganisationalUnit(Long organisationalUnitId) {
        return stubFor(
                WireMock.delete(urlPathEqualTo("/csrs/organisationalUnits/" + organisationalUnitId))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json"))
        );
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

    public StubMapping createOrganisation(String expectedInput, String response) {
        return stubFor(
                WireMock.post(urlPathEqualTo("/csrs/organisationalUnits"))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping deleteOrganisation(int organisationId) {
        return stubFor(
                WireMock.delete(urlPathEqualTo(String.format("/csrs/organisationalUnits/%s", organisationId)))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json"))
        );
    }

    public StubMapping updateOrganisation(int organisationId, String expectedInput, String response) {
        return stubFor(
                WireMock.patch(urlPathEqualTo(String.format("/csrs/organisationalUnits/%s", organisationId)))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping addDomain(int organisationId, String expectedInput, String response) {
        return stubFor(
                WireMock.post(urlPathEqualTo(String.format("/csrs/organisationalUnits/%s/domains", organisationId)))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping deleteDomain(int organisationId, int domainId, Boolean includeSubOrganisations, String response) {
        return stubFor(
                WireMock.delete(urlPathEqualTo(String.format("/csrs/organisationalUnits/%s/domains/%s", organisationId, domainId)))
                        .withQueryParam("includeSubOrgs", equalTo(includeSubOrganisations.toString()))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping addToken(int organisationId, String expectedInput, String response) {
        return stubFor(
                WireMock.post(urlPathEqualTo(String.format("/csrs/organisationalUnits/%s/agencyToken", organisationId)))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping deleteToken(int organisationId) {
        return stubFor(
                WireMock.delete(urlPathEqualTo(String.format("/csrs/organisationalUnits/%s/agencyToken", organisationId)))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json"))
        );
    }

    public StubMapping updateToken(int organisationId, String expectedInput, String response) {
        return stubFor(
                WireMock.patch(urlPathEqualTo(String.format("/csrs/organisationalUnits/%s/agencyToken", organisationId)))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }
}
