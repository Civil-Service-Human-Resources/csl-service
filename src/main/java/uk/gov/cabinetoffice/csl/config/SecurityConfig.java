package uk.gov.cabinetoffice.csl.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@Slf4j
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${oauth.jwtKey}")
    private String jwtKey;

    @Value("${management.endpoints.web.base-path}")
    private String actuatorBasePath;

    private final CustomBasicAuthenticationProvider basicAuthenticationProvider;

    public SecurityConfig(CustomBasicAuthenticationProvider basicAuthenticationProvider) {
        this.basicAuthenticationProvider = basicAuthenticationProvider;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain permittedChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Building base filter chain");
        httpSecurity.securityMatcher("/error", actuatorBasePath + "/**").cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
        return httpSecurity.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain basicAuthChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Building basic auth filter chain");
        httpSecurity.securityMatcher("/rustici/**", "/swagger-ui/**", "/v3/api-docs/**").cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeHttpRequests(requests -> requests.anyRequest().authenticated())
                .httpBasic();
        return httpSecurity.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain oauthChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Building oauth filter chain");
        httpSecurity.securityMatcher(
                        "/organisations/**",
                        "/learning/**",
                        "/courses/**",
                        "/course_records/**",
                        "/admin/**",
                        "/user/**",
                        "/reset-cache/**")
                .cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeHttpRequests(requests -> requests.anyRequest().authenticated())
                .oauth2ResourceServer().jwt(jwtSpec -> jwtSpec.decoder(jwtDecoder()));
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(basicAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(jwtKey.getBytes(), "HMACSHA256");
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
