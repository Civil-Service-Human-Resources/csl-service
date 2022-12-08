package uk.gov.cabinetoffice.csl.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("filterChain: http: {}", http.toString());
        http.cors().and().csrf().disable()
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(HttpMethod.OPTIONS, "/oauth/token").permitAll()
                        .requestMatchers(HttpMethod.GET, "/manage").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());
        DefaultSecurityFilterChain defaultSecurityFilterChain = http.build();
        log.info("filterChain: defaultSecurityFilterChain: {}", defaultSecurityFilterChain.toString());
        return defaultSecurityFilterChain;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("webSecurityCustomizer()");
        return (web) -> web.ignoring().requestMatchers("/manage");
    }

    @Bean
    public TokenStore getTokenStore(OAuthProperties oAuthProperties) {
        log.info("getTokenStore: oAuthProperties: {}", oAuthProperties.toString());
        JwtTokenStore jwtTokenStore = new JwtTokenStore(accessTokenConverter(oAuthProperties));
        log.info("getTokenStore: jwtTokenStore: {}", oAuthProperties.toString());
        return jwtTokenStore;
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter(OAuthProperties oAuthProperties) {
        log.info("accessTokenConverter: oAuthProperties: {}", oAuthProperties.toString());
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(oAuthProperties.getJwtKey());
        log.info("accessTokenConverter: jwtAccessTokenConverter: {}", jwtAccessTokenConverter.toString());
        return jwtAccessTokenConverter;
    }

    @Bean
    public OAuth2ProtectedResourceDetails resourceDetails(OAuthProperties oAuthProperties) {
        log.info("resourceDetails: oAuthProperties: {}", oAuthProperties.toString());
        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
        resource.setId("identity");
        resource.setAccessTokenUri(oAuthProperties.getTokenUrl());
        resource.setClientId(oAuthProperties.getClientId());
        resource.setClientSecret(oAuthProperties.getClientSecret());
        log.info("resourceDetails: resource: {}", resource.toString());
        return resource;
    }

    @Bean
    public PoolingHttpClientConnectionManager httpClientConnectionManager(OAuthProperties oAuthProperties) throws Exception {
        log.info("httpClientConnectionManager: oAuthProperties: {}", oAuthProperties.toString());
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(oAuthProperties.getMaxTotalConnections());
        connectionManager.setDefaultMaxPerRoute(oAuthProperties.getDefaultMaxConnectionsPerRoute());
        HttpHost host = HttpHost.create(oAuthProperties.getServiceUrl());
        connectionManager.setMaxPerRoute(new HttpRoute(host), oAuthProperties.getMaxPerServiceUrl());
        log.info("httpClientConnectionManager: connectionManager: {}", connectionManager.toString());
        return connectionManager;
    }

    @Bean
    public OAuth2RestOperations oAuthRestTemplate(OAuth2ProtectedResourceDetails resourceDetails, PoolingHttpClientConnectionManager connectionManager) {
        log.info("oAuthRestTemplate: resourceDetails: {}", resourceDetails.toString());
        log.info("oAuthRestTemplate: connectionManager: {}", connectionManager.toString());
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        AccessTokenRequest atr = new DefaultAccessTokenRequest();
        OAuth2RestTemplate oAuthRestTemplate = new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext(atr));
        oAuthRestTemplate.setRequestFactory(requestFactory);
        log.info("oAuthRestTemplate: oAuthRestTemplate: {}", oAuthRestTemplate.toString());
        return oAuthRestTemplate;
    }

    @Bean
    public RestTemplate restTemplate() {
        log.info("restTemplate");
        return new RestTemplate();
    }
}
