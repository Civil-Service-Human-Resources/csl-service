package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLinkRequest;
import uk.gov.cabinetoffice.csl.domain.rustici.RegistrationRequest;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class RusticiStubService {

    private final CslTestUtil utils;

    public RusticiStubService(CslTestUtil utils) {
        this.utils = utils;
    }

    private static UrlPathPattern getBaseUrlPathPattern(String endpoint) {
        return urlPathEqualTo(String.format("/rustici_engine/%s", endpoint));
    }

    public void postLaunchLink(String registrationId, LaunchLinkRequest expectedInput, LaunchLink response, boolean notFound) {
        String url = String.format("RusticiEngine/api/v2/registrations/%s/launchLink", registrationId);
        ResponseDefinitionBuilder responseBuilder = aResponse()
                .withHeader("Content-Type", "application/json");
        if (notFound) {
            responseBuilder.withStatus(404);
        } else {
            responseBuilder.withBody(utils.toJson(response));
        }
        stubFor(
                WireMock.post(getBaseUrlPathPattern(url))
                        .withRequestBody(equalToJson(
                                utils.toJson(expectedInput)
                        ))
                        .withHeader("EngineTenantName", equalTo("test"))
                        .willReturn(responseBuilder)
        );
    }

    public void postLaunchLinkWithRegistration(RegistrationRequest expectedInput, LaunchLink response) {
        stubFor(
                WireMock.post(getBaseUrlPathPattern("RusticiEngine/api/v2/registrations/withLaunchLink"))
                        .withRequestBody(equalToJson(
                                utils.toJson(expectedInput)
                        ))
                        .withHeader("EngineTenantName", equalTo("test"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(utils.toJson(response)))
        );
    }

}
