package uk.gov.cabinetoffice.csl.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import uk.gov.cabinetoffice.csl.client.HttpClient;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.service.auth.IBearerTokenService;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;
import uk.gov.cabinetoffice.csl.service.auth.RestTemplateOAuthInterceptor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Import({MockClockConfig.class})
public class TestConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().anyRequest();
    }

    @Bean
    @Primary
    public IBearerTokenService getBearerTokenService() {
        IBearerTokenService fakeTokenService = mock(IBearerTokenService.class);
        when(fakeTokenService.getBearerToken()).thenReturn("fakeToken");
        return fakeTokenService;
    }

    @Bean(name = "stubLearnerRecordHttpClient")
    @Primary
    public IHttpClient learnerRecordClient(@Value("${learnerRecord.serviceUrl}") String baseUri) {
        RestTemplateOAuthInterceptor interceptor = new RestTemplateOAuthInterceptor(getBearerTokenService());
        return new HttpClient(
                new RestTemplateBuilder()
                        .rootUri(baseUri)
                        .additionalInterceptors(interceptor)
                        .build());
    }

    @Bean(name = "stubLearningCatalogueHttpClient")
    @Primary
    public IHttpClient learningCatalogueClient(@Value("${learningCatalogue.serviceUrl}") String baseUri) {
        RestTemplateOAuthInterceptor interceptor = new RestTemplateOAuthInterceptor(getBearerTokenService());
        return new HttpClient(
                new RestTemplateBuilder()
                        .rootUri(baseUri)
                        .additionalInterceptors(interceptor)
                        .build());
    }

    @Bean(name = "stubRusticiHttpClient")
    @Primary
    IHttpClient rusticiClient(@Value("${rustici.serviceUrl}") String baseUri) {
        return new HttpClient(
                new RestTemplateBuilder()
                        .rootUri(baseUri)
                        .basicAuthentication("user", "pass")
                        .additionalInterceptors((request, body, execution) -> {
                            request.getHeaders().add("EngineTenantName", "test");
                            return execution.execute(request, body);
                        })
                        .build());
    }

    @Bean
    @Primary
    public IUserAuthService fakeAuthService() {
        IUserAuthService authService = mock(IUserAuthService.class);
        when(authService.getUsername()).thenReturn("userId");
        return authService;
    }

}
