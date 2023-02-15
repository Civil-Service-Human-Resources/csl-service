package uk.gov.cabinetoffice.csl.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.domain.CourseRecordInput;
import uk.gov.cabinetoffice.csl.domain.ModuleRecordInput;
import uk.gov.cabinetoffice.csl.domain.RegistrationInput;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.RusticiService;

import static uk.gov.cabinetoffice.csl.CslServiceUtil.returnError;

@Slf4j
@RestController
@RequestMapping(path = "/csl-test")
public class CslServiceTestController {

    private final LearnerRecordService learnerRecordService;

    private final RusticiService rusticiService;

    public CslServiceTestController(LearnerRecordService learnerRecordService, RusticiService rusticiService) {
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
            return returnError(HttpStatus.BAD_REQUEST, "Learner Id is missing from authentication token",
                    "/course-records");
        }
        return learnerRecordService.getCourseRecordForLearner(learnerId, courseId);
    }

    @PostMapping(path = "/course-record", produces = "application/json")
    public ResponseEntity<?> createCourseRecordForLearner(@Valid @RequestBody CourseRecordInput courseRecordInput) {
        return learnerRecordService.createCourseRecordForLearner(courseRecordInput);
    }

    @PostMapping(path = "/module-record", produces = "application/json")
    public ResponseEntity<?> createModuleRecordForLearner(@Valid @RequestBody ModuleRecordInput moduleRecordInput) {
        return learnerRecordService.createModuleRecordForLearner(moduleRecordInput);
    }

    //Only three inputs are required: registrationId, courseId and moduleId
    @PostMapping(path = "/launch-link", produces = "application/json")
    public ResponseEntity<?> getRegistrationLaunchLink(@Valid @RequestBody RegistrationInput registrationInput) {
        log.debug("registrationId: {}", registrationInput.getRegistrationId());
        return rusticiService.getRegistrationLaunchLink(registrationInput);
    }

    //Only four inputs are required: registrationId, courseId, moduleId and learnerFirstName
    @PostMapping(path = "/registration-launch-link", produces = "application/json")
    public ResponseEntity<?> createRegistrationAndLaunchLink(@Valid @RequestBody RegistrationInput registrationInput,
                                                             Authentication authentication) {
        log.debug("registrationId: {}", registrationInput.getRegistrationId());
        String learnerId = getLearnerIdFromAuth(authentication);
        registrationInput.setLearnerId(learnerId);
        if(StringUtils.isBlank(learnerId)) {
            return returnError(HttpStatus.BAD_REQUEST,"Learner Id is missing from authentication token",
                    "/registration-launch-link");
        }
        return rusticiService.createRegistrationAndLaunchLink(registrationInput);
    }

    private String getLearnerIdFromAuth(Authentication authentication) {
        String learnerId = null;
        if(authentication != null) {
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            learnerId = (String)jwtPrincipal.getClaims().get("user_name");
        }
        log.debug("Learner Id from authentication token: {}", learnerId);
        return learnerId;
    }
}
