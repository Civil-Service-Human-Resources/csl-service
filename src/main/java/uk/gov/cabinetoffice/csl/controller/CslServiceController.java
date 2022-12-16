package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping(path = "/csl")
public class CslServiceController {

    @GetMapping(path = "/test/{input}", produces = "application/json")
    public ResponseEntity<String> test(@PathVariable("input") String input, Authentication authentication) {
        log.debug("Input: {}", input);
        log.debug("authentication: {}", authentication);
        if(authentication != null) {
            log.debug("Is authenticated?: {}", authentication.isAuthenticated());
            log.debug("Authentication name: {}", authentication.getName());
            log.debug("Authentication principal: {}", authentication.getPrincipal());
            WebAuthenticationDetails authenticationDetails = (WebAuthenticationDetails)authentication.getDetails();
            log.debug("Authentication details: {}", authenticationDetails);
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            log.debug("Authentication jwtPrincipal: {}", jwtPrincipal);
            Map<String, Object> claims = jwtPrincipal.getClaims();
            log.debug("Authentication jwtPrincipal Claims: {}", claims);
            log.debug("Authentication jwtPrincipal Claims user_name: {}", claims.get("user_name"));
            List<String> authorities = (List)claims.get("authorities");
            log.debug("Authentication jwtPrincipal Claims authorities: {}", authorities);
            log.debug("Authentication jwtPrincipal Claims client_id: {}", claims.get("client_id"));
            Map<String, Object> headers = jwtPrincipal.getHeaders();
            log.debug("Authentication jwtPrincipal Headers: {}", headers);
            log.debug("Authentication jwtPrincipal Headers typ: {}", headers.get("typ"));
            log.debug("Authentication jwtPrincipal Headers alg: {}", headers.get("alg"));
            log.debug("Authentication jwtPrincipal ExpiresAt: {}", jwtPrincipal.getExpiresAt());
            log.debug("Authentication jwtPrincipal Id: {}", jwtPrincipal.getId());
            log.debug("Authentication jwtPrincipal IssuedAt: {}", jwtPrincipal.getIssuedAt());
            log.debug("Authentication jwtPrincipal TokenValue: {}", jwtPrincipal.getTokenValue());
            log.debug("Authentication jwtPrincipal Issuer: {}", jwtPrincipal.getIssuer());
            log.debug("Authentication jwtPrincipal NotBefore: {}", jwtPrincipal.getNotBefore());
            log.debug("Authentication jwtPrincipal Subject: {}", jwtPrincipal.getSubject());
            log.debug("Authentication jwtPrincipal Audience: {}", jwtPrincipal.getAudience());
        }
        return new ResponseEntity<>(input, OK);
    }
}
