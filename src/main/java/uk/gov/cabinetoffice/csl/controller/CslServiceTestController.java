package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.service.IdentityService;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
public class CslServiceTestController {

    private final IdentityService identityService;

    public CslServiceTestController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @GetMapping(path = "/test/{input}", produces = "application/json")
    public ResponseEntity<?> test(@PathVariable("input") String input) throws Exception {
        log.info("Input: {}", input);
        return new ResponseEntity<>(identityService.getOAuthServiceToken(), OK);
        //return identityService.getClientAccessTokenFromIdentityService();
        //return identityService.getOAuthServiceToken();
    }
}
