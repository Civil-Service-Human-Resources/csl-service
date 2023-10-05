package uk.gov.cabinetoffice.csl.client.RusticiEngineClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.client.HttpClient;
import uk.gov.cabinetoffice.csl.client.IHttpClient;

@Configuration
public class RusticiEngineClientConfig {

    @Value("${rustici.serviceUrl}")
    private String rusticiBaseUrl;

    @Value("${rustici.username}")
    private String rusticiUsername;

    @Value("${rustici.password}")
    private String rusticiPassword;

    @Value("${rustici.engineTenantName}")
    private String rusticiEngineTenantName;

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
}
