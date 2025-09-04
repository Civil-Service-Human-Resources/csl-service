package uk.gov.cabinetoffice.csl.client.frontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.client.HttpClient;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.service.auth.RestTemplateOAuthInterceptor;

@Configuration
public class FrontendClientConfig {

    private final String lpgUiBaseUrl;

    private final RestTemplateOAuthInterceptor restTemplateOAuthInterceptor;

    public FrontendClientConfig(@Value("${ui.lpg.baseUrl}") String lpgUiBaseUrl, RestTemplateOAuthInterceptor restTemplateOAuthInterceptor) {
        this.lpgUiBaseUrl = lpgUiBaseUrl;
        this.restTemplateOAuthInterceptor = restTemplateOAuthInterceptor;
    }

    @Bean(name = "lpgUiClient")
    IHttpClient identityClient(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(lpgUiBaseUrl)
                .interceptors(restTemplateOAuthInterceptor)
                .build();
        return new HttpClient(restTemplate);
    }

}
