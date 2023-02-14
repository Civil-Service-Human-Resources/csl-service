package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.domain.ErrorResponse;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.RusticiService;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping(path = "/csl")
public class CslServiceController {

    private final LearnerRecordService learnerRecordService;

    private final RusticiService rusticiService;

    public CslServiceController(LearnerRecordService learnerRecordService, RusticiService rusticiService) {
        this.learnerRecordService = learnerRecordService;
        this.rusticiService = rusticiService;
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
            log.debug("Authentication jwtPrincipal user_name: {}", jwtPrincipal.getClaims().get("user_name"));
            log.debug("Authentication jwtPrincipal Headers: {}",  jwtPrincipal.getHeaders());
            log.debug("Authentication jwtPrincipal ExpiresAt: {}", jwtPrincipal.getExpiresAt());
            log.debug("Authentication jwtPrincipal Id: {}", jwtPrincipal.getId());
            log.debug("Authentication jwtPrincipal IssuedAt: {}", jwtPrincipal.getIssuedAt());
            log.debug("Authentication jwtPrincipal TokenValue: {}", jwtPrincipal.getTokenValue());
        }
        return new ResponseEntity<>(input, HttpStatus.OK);
    }

    @GetMapping(path = "/course-records", produces = "application/json")
    public ResponseEntity<?> getCourseRecordForLearner(@RequestParam String courseId, Authentication authentication) {
        log.debug("courseId: {}", courseId);
        String learnerId = getLearnerIdFromAuth(authentication);
        if(StringUtils.isBlank(learnerId)) {
            return returnError(HttpStatus.BAD_REQUEST, "Authentication token is missing from the request",
                    "/course-records");
        }
        return learnerRecordService.getCourseRecordForLearner(learnerId, courseId);
    }

    @GetMapping(path = "/launch-link", produces = "application/json")
    public ResponseEntity<?> getRegistrationLaunchLink(@RequestParam String registrationId,
                                                       @RequestParam String courseId,
                                                       @RequestParam String moduleId) {
        log.debug("registrationId: {}", registrationId);
        return rusticiService.getRegistrationLaunchLink(registrationId, courseId, moduleId);
    }

    @GetMapping(path = "/registration-launch-link", produces = "application/json")
    public ResponseEntity<?> createRegistrationAndLaunchLink(@RequestParam String registrationId,
                                                             @RequestParam String courseId,
                                                             @RequestParam String moduleId,
                                                             @RequestParam String learnerFirstName,
                                                             Authentication authentication) {
        log.debug("registrationId: {}", registrationId);
        String learnerId = getLearnerIdFromAuth(authentication);
        if(StringUtils.isBlank(learnerId)) {
            return returnError(HttpStatus.BAD_REQUEST,"Authentication token is missing from the request",
                    "/registration-launch-link");
        }
        return rusticiService.createRegistrationAndLaunchLink(registrationId, courseId, moduleId, learnerFirstName, learnerId);
    }

    private String getLearnerIdFromAuth(Authentication authentication) {
        String learnerId = null;
        if(authentication != null) {
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            learnerId = (String)jwtPrincipal.getClaims().get("user_name");
        }
        log.debug("learnerId from Authentication: {}", learnerId);
        return learnerId;
    }

    private ResponseEntity<?> returnError(HttpStatus httpStatus, String errorMessage, String path) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.toString(),
                errorMessage, path);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
