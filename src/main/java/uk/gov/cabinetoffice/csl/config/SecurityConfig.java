package uk.gov.cabinetoffice.csl.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableResourceServer
@EnableWebSecurity
@EnableOAuth2Client
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${management.endpoints.web.base-path}")
    private String actuatorBasePath;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.debug("configure(HttpSecurity http): http: {}", http.toString());
        log.debug("configure(HttpSecurity http): actuatorBasePath: {}", actuatorBasePath);
        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.GET, actuatorBasePath, actuatorBasePath + "/**").permitAll()
                .anyRequest().authenticated();
        log.debug("configure(HttpSecurity http): End");
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception{
        log.debug("configure(WebSecurity webSecurity): webSecurity: {}", webSecurity.toString());
        log.debug("configure(WebSecurity webSecurity): actuatorBasePath: {}", actuatorBasePath);
        webSecurity.ignoring().antMatchers(HttpMethod.GET, actuatorBasePath, actuatorBasePath + "/**");
        log.debug("configure(WebSecurity webSecurity): webSecurity: End");
    }

    @Bean
    public TokenStore getTokenStore(OAuthProperties oAuthProperties) {
        log.debug("getTokenStore: oAuthProperties: {}", oAuthProperties.toString());
        JwtTokenStore jwtTokenStore = new JwtTokenStore(accessTokenConverter(oAuthProperties));
        log.debug("getTokenStore: jwtTokenStore: {}", oAuthProperties.toString());
        return jwtTokenStore;
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter(OAuthProperties oAuthProperties) {
        log.debug("accessTokenConverter: oAuthProperties: {}", oAuthProperties.toString());
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(oAuthProperties.getJwtKey());
        log.debug("accessTokenConverter: jwtAccessTokenConverter: {}", jwtAccessTokenConverter.toString());
        return jwtAccessTokenConverter;
    }

    @Bean
    public OAuth2ProtectedResourceDetails resourceDetails(OAuthProperties oAuthProperties) {
        log.debug("resourceDetails: oAuthProperties: {}", oAuthProperties.toString());
        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
        resource.setId("identity");
        resource.setAccessTokenUri(oAuthProperties.getTokenUrl());
        resource.setClientId(oAuthProperties.getClientId());
        resource.setClientSecret(oAuthProperties.getClientSecret());
        log.debug("resourceDetails: resource: {}", resource.toString());
        return resource;
    }

    @Bean
    public PoolingHttpClientConnectionManager httpClientConnectionManager(OAuthProperties oAuthProperties) {
        log.debug("httpClientConnectionManager: oAuthProperties: {}", oAuthProperties.toString());
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(oAuthProperties.getMaxTotalConnections());
        connectionManager.setDefaultMaxPerRoute(oAuthProperties.getDefaultMaxConnectionsPerRoute());
        HttpHost host = new HttpHost(oAuthProperties.getServiceUrl());
        connectionManager.setMaxPerRoute(new HttpRoute(host), oAuthProperties.getMaxPerServiceUrl());
        log.debug("httpClientConnectionManager: connectionManager: {}", connectionManager.toString());
        return connectionManager;
    }

    @Bean
    public OAuth2RestOperations oAuthRestTemplate(OAuth2ProtectedResourceDetails resourceDetails, PoolingHttpClientConnectionManager connectionManager) {
        log.debug("oAuthRestTemplate: resourceDetails: {}", resourceDetails.toString());
        log.debug("oAuthRestTemplate: connectionManager: {}", connectionManager.toString());
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setConnectionManager(connectionManager)
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        AccessTokenRequest atr = new DefaultAccessTokenRequest();
        OAuth2RestTemplate oAuthRestTemplate = new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext(atr));
        oAuthRestTemplate.setRequestFactory(requestFactory);
        log.debug("oAuthRestTemplate: oAuthRestTemplate: {}", oAuthRestTemplate.toString());
        return oAuthRestTemplate;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
