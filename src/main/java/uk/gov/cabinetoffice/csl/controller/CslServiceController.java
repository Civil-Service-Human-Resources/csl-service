package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
            log.debug("Authentication principal: {}", authentication.getPrincipal());
            log.debug("Authentication name: {}", authentication.getName());
            OAuth2AuthenticationDetails authenticationDetails = (OAuth2AuthenticationDetails)authentication.getDetails();
            log.debug("Token Type: {}", authenticationDetails.getTokenType());
            log.debug("Token Value: {}", authenticationDetails.getTokenValue());
            log.debug("Decoded Details: {}", authenticationDetails.getDecodedDetails());
            List<GrantedAuthority> grantedAuthorities = (List<GrantedAuthority>)authentication.getAuthorities();
            log.debug("Authorities: {}", grantedAuthorities);
            grantedAuthorities.forEach(ga -> log.debug("Authority: {}", ga.getAuthority()));
        }
        return new ResponseEntity<>(input, OK);
    }
}
