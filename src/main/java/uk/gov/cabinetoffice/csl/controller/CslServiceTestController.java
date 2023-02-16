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
    public ResponseEntity<?> test(@PathVariable("input") String input, Authentication authentication) {
        log.debug("Input: {}", input);
        log.debug("learnerId: {}", getLearnerIdFromAuth(authentication));
        return new ResponseEntity<>(input, HttpStatus.OK);
    }

    @GetMapping(path = "/course-records", produces = "application/json")
    public ResponseEntity<?> getCourseRecordForLearner(@RequestParam String courseId, Authentication authentication) {
        log.debug("courseId: {}", courseId);
        String learnerId = getLearnerIdFromAuth(authentication);
        if(StringUtils.isBlank(learnerId)) {
            return returnError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Learner Id is missing from authentication token","/course-records");
        }
        return learnerRecordService.getCourseRecordForLearner(learnerId, courseId);
    }

    @PostMapping(path = "/course-record", produces = "application/json")
    public ResponseEntity<?> createCourseRecordForLearner(@Valid @RequestBody CourseRecordInput courseRecordInput,
                                                          Authentication authentication) {
        log.debug("courseId: {}", courseRecordInput.getCourseId());
        String learnerId = getLearnerIdFromAuth(authentication);
        if(StringUtils.isBlank(learnerId)) {
            return returnError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Learner Id is missing from authentication token","/course-record");
        }
        courseRecordInput.setUserId(learnerId);
        courseRecordInput.getModuleRecords().forEach(m -> m.setUserId(learnerId));
        return learnerRecordService.createCourseRecordForLearner(courseRecordInput);
    }

    @PostMapping(path = "/course-record/update", produces = "application/json")
    public ResponseEntity<?> updateCourseRecordForLearner(@RequestParam String courseId, Authentication authentication) {
        log.debug("courseId: {}", courseId);
        String learnerId = getLearnerIdFromAuth(authentication);
        if(StringUtils.isBlank(learnerId)) {
            return returnError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Learner Id is missing from authentication token","/course-record");
        }
        return learnerRecordService.updateCourseRecordForLearner(learnerId, courseId);
    }

    @PostMapping(path = "/module-record", produces = "application/json")
    public ResponseEntity<?> createModuleRecordForLearner(@Valid @RequestBody ModuleRecordInput moduleRecordInput,
                                                          Authentication authentication) {
        log.debug("courseId: {}", moduleRecordInput.getCourseId());
        String learnerId = getLearnerIdFromAuth(authentication);
        if(StringUtils.isBlank(learnerId)) {
            return returnError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Learner Id is missing from authentication token","/module-record");
        }
        moduleRecordInput.setUserId(learnerId);
        return learnerRecordService.createModuleRecordForLearner(moduleRecordInput);
    }

//    @PatchMapping(path = "/module-record/{moduleRecordId}", consumes = "application/json-patch+json", produces = "application/json")
//    public ResponseEntity<?> patchModuleRecordForLearner(@PathVariable("moduleRecordId") Long moduleRecordId,
//                                                         @Valid @RequestBody AdditionalValue additionalValue) {
//        Map<>
//        return learnerRecordService.updateModuleRecordForLearner(moduleRecordId, patchModuleRecordInput);
//    }

    //Only three inputs are required: registrationId, courseId and moduleId
    @PostMapping(path = "/launch-link", produces = "application/json")
    public ResponseEntity<?> getRegistrationLaunchLink(@Valid @RequestBody RegistrationInput registrationInput,
                                                       Authentication authentication) {
        log.debug("registrationId: {}", registrationInput.getRegistrationId());
        String learnerId = getLearnerIdFromAuth(authentication);
        registrationInput.setLearnerId(learnerId);
        if(StringUtils.isBlank(learnerId)) {
            return returnError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Learner Id is missing from authentication token","/launch-link");
        }
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
            return returnError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Learner Id is missing from authentication token","/registration-launch-link");
        }
        return rusticiService.createRegistrationAndLaunchLink(registrationInput);
    }
}
