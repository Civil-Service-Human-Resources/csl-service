package uk.gov.cabinetoffice.csl.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@Slf4j
public class SecurityConfig {

    @Value("${oauth.jwtKey}")
    private String jwtKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("filterChain: http: {}", http.toString());
        http.cors().and().csrf().disable()
                .authorizeHttpRequests((authz) -> authz
                        .anyRequest().authenticated()
                )
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .oauth2ResourceServer()
                .jwt(jwtSpec -> {jwtSpec.decoder(jwtDecoder());});

        DefaultSecurityFilterChain defaultSecurityFilterChain = http.build();
        log.debug("filterChain: defaultSecurityFilterChain: {}", defaultSecurityFilterChain.toString());
        return defaultSecurityFilterChain;
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
