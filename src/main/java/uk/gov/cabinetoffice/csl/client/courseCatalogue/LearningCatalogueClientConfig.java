package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.client.HttpClient;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.service.auth.RestTemplateOAuthInterceptor;

@Configuration
public class LearningCatalogueClientConfig {

    @Value("${learningCatalogue.serviceUrl}")
    private String learningCatalogueBaseUrl;

    private final RestTemplateOAuthInterceptor restTemplateOAuthInterceptor;

    public LearningCatalogueClientConfig(RestTemplateOAuthInterceptor restTemplateOAuthInterceptor) {
        this.restTemplateOAuthInterceptor = restTemplateOAuthInterceptor;
    }

    @Bean(name = "learningCatalogueHttpClient")
    IHttpClient learningCatalogueClient(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(learningCatalogueBaseUrl)
                .additionalInterceptors(restTemplateOAuthInterceptor)
                .build();
        return new HttpClient(restTemplate);
    }
}
