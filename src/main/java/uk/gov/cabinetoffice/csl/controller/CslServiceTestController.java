package uk.gov.cabinetoffice.csl.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.domain.*;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.RusticiService;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.getLearnerIdFromAuth;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.returnError;

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
        getLearnerIdFromAuth(authentication);
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

    @PatchMapping(path = "/course-record", consumes = "application/json-patch+json", produces = "application/json")
    public ResponseEntity<?> patchCourseRecordForLearner(@RequestParam String learnerId, @RequestParam String courseId,
                                                         @Valid @RequestBody PatchCourseRecordInput patchCourseRecordInput) {
        return learnerRecordService.updateCourseRecordForLearner(learnerId, courseId, patchCourseRecordInput);
    }

    @PostMapping(path = "/module-record", produces = "application/json")
    public ResponseEntity<?> createModuleRecordForLearner(@Valid @RequestBody ModuleRecordInput moduleRecordInput) {
        return learnerRecordService.createModuleRecordForLearner(moduleRecordInput);
    }

    @PatchMapping(path = "/module-record/{moduleRecordId}", consumes = "application/json-patch+json", produces = "application/json")
    public ResponseEntity<?> patchModuleRecordForLearner(@PathVariable("moduleRecordId") Long moduleRecordId,
                                                         @Valid @RequestBody PatchModuleRecordInput patchModuleRecordInput) {
        return learnerRecordService.updateModuleRecordForLearner(moduleRecordId, patchModuleRecordInput);
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
}
