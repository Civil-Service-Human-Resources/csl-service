package uk.gov.cabinetoffice.csl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class SecurityConfig {

    @Value("${oauth.jwtKey}")
    private String jwtKey;

    private CustomBasicAuthenticationProvider basicAuthenticationProvider;

    public SecurityConfig(CustomBasicAuthenticationProvider basicAuthenticationProvider) {
        this.basicAuthenticationProvider = basicAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeHttpRequests().requestMatchers("/test/**").authenticated()
                .and().httpBasic()
                .and().authorizeHttpRequests().requestMatchers("/courses/**").authenticated()
                .and().oauth2ResourceServer().jwt(jwtSpec -> jwtSpec.decoder(jwtDecoder()))
                .and().build();
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
}
