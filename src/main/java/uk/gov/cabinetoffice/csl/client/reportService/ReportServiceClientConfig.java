package uk.gov.cabinetoffice.csl.client.reportService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.client.HttpClient;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.service.auth.RestTemplateOAuthInterceptor;

@Configuration
public class ReportServiceClientConfig {

    @Value("${reportService.serviceUrl}")
    private String reportServiceBaseUrl;

    private final RestTemplateOAuthInterceptor restTemplateOAuthInterceptor;

    public ReportServiceClientConfig(RestTemplateOAuthInterceptor restTemplateOAuthInterceptor) {
        this.restTemplateOAuthInterceptor = restTemplateOAuthInterceptor;
    }

    @Bean(name = "reportServiceHttpClient")
    IHttpClient learnerRecordClient(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(reportServiceBaseUrl)
                .additionalInterceptors(restTemplateOAuthInterceptor)
                .build();
        return new HttpClient(restTemplate);
    }
}
