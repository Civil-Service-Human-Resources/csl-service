package uk.gov.cabinetoffice.csl.client.identity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.client.ParallelHttpClient;
import uk.gov.cabinetoffice.csl.service.auth.IBearerTokenService;
import uk.gov.cabinetoffice.csl.service.auth.RestTemplateOAuthInterceptor;

@Configuration
public class IdentityAPIClientConfig {

    @Value("${oauth.serviceUrl}")
    private String identityBaseUrl;

    private final RestTemplateOAuthInterceptor restTemplateOAuthInterceptor;
    private final IBearerTokenService bearerTokenService;

    public IdentityAPIClientConfig(RestTemplateOAuthInterceptor restTemplateOAuthInterceptor, IBearerTokenService bearerTokenService) {
        this.restTemplateOAuthInterceptor = restTemplateOAuthInterceptor;
        this.bearerTokenService = bearerTokenService;
    }

    @Bean(name = "identityOAuthHttpClient")
    IHttpClient identityOAuthClient(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(identityBaseUrl)
                .additionalInterceptors(restTemplateOAuthInterceptor)
                .build();
        return new ParallelHttpClient(restTemplate, bearerTokenService);
    }

}
