package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.springframework.stereotype.Service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class FrontendStubService {

    private static UrlPathPattern getBaseUrlPathPattern(String endpoint) {
        return urlPathEqualTo(String.format("/lpg_ui/%s", endpoint));
    }

    public StubMapping clearLearningCache(String userId, String courseId) {
        MappingBuilder mappingBuilder = WireMock.post(getBaseUrlPathPattern(String.format("caches/user/%s/clear-learning/%s", userId, courseId)));
        return stubFor(
                mappingBuilder
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json"))
        );
    }

}
