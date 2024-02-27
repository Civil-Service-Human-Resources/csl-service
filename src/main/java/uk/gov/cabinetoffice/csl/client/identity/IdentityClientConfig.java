package uk.gov.cabinetoffice.csl.client.identity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.client.HttpClient;
import uk.gov.cabinetoffice.csl.client.IHttpClient;

@Configuration
public class IdentityClientConfig {

    @Value("${oauth.serviceUrl}")
    private String identityBaseUrl;

    @Value("${oauth.clientId}")
    private String identityClientId;

    @Value("${oauth.clientSecret}")
    private String identityClientSecret;

    @Bean(name = "identityHttpClient")
    IHttpClient identityClient(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(identityBaseUrl)
                .basicAuthentication(identityClientId, identityClientSecret)
                .build();
        return new HttpClient(restTemplate);
    }

}
