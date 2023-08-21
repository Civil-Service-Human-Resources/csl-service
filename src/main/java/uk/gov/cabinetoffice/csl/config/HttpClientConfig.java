package uk.gov.cabinetoffice.csl.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.client.HttpClient;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.service.auth.RestTemplateOAuthInterceptor;

@Configuration
public class HttpClientConfig {

    @Value("${oauth.tokenUrl}")
    private String identityBaseUrl;

    @Value("${oauth.clientId}")
    private String identityClientId;

    @Value("${oauth.clientSecret}")
    private String identityClientSecret;

    @Value("${learningCatalogue.serviceUrl}")
    private String learningCatalogueBaseUrl;

    @Value("${learnerRecord.serviceUrl}")
    private String learnerRecordBaseUrl;

    @Value("${rustici.serviceUrl}")
    private String rusticiBaseUrl;

    @Value("${rustici.username}")
    private String rusticiUsername;

    @Value("${rustici.password}")
    private String rusticiPassword;

    @Value("${rustici.engineTenantName}")
    private String rusticiEngineTenantName;

    private final RestTemplateOAuthInterceptor restTemplateOAuthInterceptor;

    public HttpClientConfig(RestTemplateOAuthInterceptor restTemplateOAuthInterceptor) {
        this.restTemplateOAuthInterceptor = restTemplateOAuthInterceptor;
    }

    @Bean
    public Module javaTimeModule() {
        return new JavaTimeModule();
    }

    @Bean(name = "rusticiHttpClient")
    IHttpClient rusticiClient(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(rusticiBaseUrl)
                .basicAuthentication(rusticiUsername, rusticiPassword)
                .additionalInterceptors((request, body, execution) -> {
                    request.getHeaders().add("EngineTenantName", rusticiEngineTenantName);
                    return execution.execute(request, body);
                })
                .build();
        return new HttpClient(restTemplate);
    }

    @Bean(name = "learnerRecordHttpClient")
    IHttpClient learnerRecordClient(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(learnerRecordBaseUrl)
                .additionalInterceptors(restTemplateOAuthInterceptor)
                .build();
        return new HttpClient(restTemplate);
    }

    @Bean(name = "identityHttpClient")
    IHttpClient identityClient(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(identityBaseUrl)
                .basicAuthentication(identityClientId, identityClientSecret)
                .build();
        return new HttpClient(restTemplate);
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
