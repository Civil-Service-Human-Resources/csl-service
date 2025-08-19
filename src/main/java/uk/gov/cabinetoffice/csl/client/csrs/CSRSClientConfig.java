package uk.gov.cabinetoffice.csl.client.csrs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.client.ParallelHttpClient;
import uk.gov.cabinetoffice.csl.service.auth.IBearerTokenService;
import uk.gov.cabinetoffice.csl.service.auth.RestTemplateOAuthInterceptor;

@Configuration
public class CSRSClientConfig {

    @Value("${csrs.serviceUrl}")
    private String csrsBaseUrl;

    private final RestTemplateOAuthInterceptor restTemplateOAuthInterceptor;
    private final IBearerTokenService bearerTokenService;

    public CSRSClientConfig(RestTemplateOAuthInterceptor restTemplateOAuthInterceptor, IBearerTokenService bearerTokenService) {
        this.restTemplateOAuthInterceptor = restTemplateOAuthInterceptor;
        this.bearerTokenService = bearerTokenService;
    }

    @Bean(name = "csrsHttpClient")
    ParallelHttpClient csrsClient(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(csrsBaseUrl)
                .additionalInterceptors(restTemplateOAuthInterceptor)
                .build();
        return new ParallelHttpClient(restTemplate, bearerTokenService);
    }
}
