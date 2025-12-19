package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.springframework.stereotype.Service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class IdentityAPIServiceStubService {

    public StubMapping getAgencyTokenSpacesUsed(String agencyTokenUid, Integer capacityUsed) {
        return stubFor(
                WireMock.get(urlPathEqualTo("/identity/agency/" + agencyTokenUid))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format("""
                                            {"capacityUsed": %s}
                                        """, capacityUsed)))
        );
    }

    public StubMapping getIdentityWithEmail(String userEmail, String identityDtoResponse) {
        return stubFor(
                WireMock.get(urlPathEqualTo("/identity/api/identities"))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .withQueryParam("emailAddress", equalTo(userEmail))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(identityDtoResponse))
        );
    }
}
