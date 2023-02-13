package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

@Slf4j
@RestController
@RequestMapping(path = "/csl")
public class CslServiceController {

    private final LearnerRecordService learnerRecordService;

    public CslServiceController(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    @GetMapping(path = "/test/{input}", produces = "application/json")
    public ResponseEntity<String> test(@PathVariable("input") String input, Authentication authentication) {
        log.debug("Input: {}", input);
        log.debug("Authentication: {}", authentication);
        if(authentication != null) {
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            log.debug("Authenticated?: {}", authentication.isAuthenticated());
            log.debug("Authentication jwtPrincipal: {}", jwtPrincipal);
            log.debug("Authentication jwtPrincipal Claims: {}", jwtPrincipal.getClaims());
            log.debug("Authentication jwtPrincipal Headers: {}",  jwtPrincipal.getHeaders());
            log.debug("Authentication jwtPrincipal ExpiresAt: {}", jwtPrincipal.getExpiresAt());
            log.debug("Authentication jwtPrincipal Id: {}", jwtPrincipal.getId());
            log.debug("Authentication jwtPrincipal IssuedAt: {}", jwtPrincipal.getIssuedAt());
            log.debug("Authentication jwtPrincipal TokenValue: {}", jwtPrincipal.getTokenValue());
        }
        return new ResponseEntity<>(input, HttpStatus.OK);
    }

    @GetMapping(path = "/course_records", produces = "application/json")
    public ResponseEntity<?> getCourseRecordForLearner(@RequestParam String learnerId, @RequestParam String courseId) {
        log.debug("learnerId: {}, courseId: {}", learnerId, courseId);
        return learnerRecordService.getCourseRecordForLearner(learnerId, courseId);
    }
}
