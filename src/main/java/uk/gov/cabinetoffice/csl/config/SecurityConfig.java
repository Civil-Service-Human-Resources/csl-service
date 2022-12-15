package uk.gov.cabinetoffice.csl.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
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

    @Value("${oauth.tokenUrl}")
    private String tokenUrl;

    @Value("${oauth.clientId}")
    private String clientId;

    @Value("${oauth.clientSecret}")
    private String clientSecret;

    @Value("${oauth.jwtKey}")
    private String jwtKey;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.debug("configure(HttpSecurity http): http: {}", http.toString());
        log.debug("configure(HttpSecurity http): actuatorBasePath: {}", actuatorBasePath);
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest().authenticated();
        log.debug("configure(HttpSecurity http): End");
    }

    @Bean
    public TokenStore getTokenStore() {
        log.debug("getTokenStore: start");
        JwtTokenStore jwtTokenStore = new JwtTokenStore(accessTokenConverter());
        log.debug("getTokenStore: jwtTokenStore: {}", jwtTokenStore.toString());
        return jwtTokenStore;
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        log.debug("accessTokenConverter: start");
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(this.jwtKey);
        log.debug("accessTokenConverter: jwtAccessTokenConverter: {}", jwtAccessTokenConverter.toString());
        return jwtAccessTokenConverter;
    }

    @Bean
    public OAuth2ProtectedResourceDetails resourceDetails() {
        log.debug("resourceDetails: start");
        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
        resource.setId("identity");
        resource.setAccessTokenUri(tokenUrl);
        resource.setClientId(clientId);
        resource.setClientSecret(clientSecret);
        log.debug("resourceDetails: resource: {}", resource.toString());
        return resource;
    }

    @Bean
    public OAuth2RestOperations oAuthRestTemplate(OAuth2ProtectedResourceDetails resourceDetails) {
        AccessTokenRequest atr = new DefaultAccessTokenRequest();
        return new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext(atr));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
