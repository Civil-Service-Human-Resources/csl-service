package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLinkRequest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static uk.gov.cabinetoffice.csl.util.CslTestUtil.toJson;

public class RusticiStubService {

    private static UrlPathPattern getBaseUrlPathPattern(String endpoint) {
        return urlPathEqualTo(String.format("/rustici_engine/%s", endpoint));
    }

    public static void postLaunchLink(String registrationId, LaunchLinkRequest expectedInput, LaunchLink response) {
        String url = String.format("RusticiEngine/api/v2/registrations/%s/launchLink", registrationId);
        stubFor(
                WireMock.post(getBaseUrlPathPattern(url))
                        .withRequestBody(equalToJson(
                                toJson(expectedInput)
                        ))
                        .withHeader("EngineTenantName", equalTo("test"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(toJson(response)))
        );
    }

}
