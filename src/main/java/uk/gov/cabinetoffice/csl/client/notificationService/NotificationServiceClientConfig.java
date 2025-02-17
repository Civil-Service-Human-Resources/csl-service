package uk.gov.cabinetoffice.csl.client.notificationService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.client.HttpClient;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.service.auth.RestTemplateOAuthInterceptor;

@Configuration
public class NotificationServiceClientConfig {

    @Value("${notificationService.serviceUrl}")
    private String notificationServiceBaseUrl;

    private final RestTemplateOAuthInterceptor restTemplateOAuthInterceptor;

    public NotificationServiceClientConfig(RestTemplateOAuthInterceptor restTemplateOAuthInterceptor) {
        this.restTemplateOAuthInterceptor = restTemplateOAuthInterceptor;
    }

    @Bean(name = "notificationServiceHttpClient")
    IHttpClient notificationServiceClient(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(notificationServiceBaseUrl)
                .additionalInterceptors(restTemplateOAuthInterceptor)
                .build();
        return new HttpClient(restTemplate);
    }
}
