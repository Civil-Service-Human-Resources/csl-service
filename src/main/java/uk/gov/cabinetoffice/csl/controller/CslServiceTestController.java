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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
                    "Learner Id is missing from authentication token","/course-records", null);
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
                    "Learner Id is missing from authentication token","/course-record", null);
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
                    "Learner Id is missing from authentication token","/course-record/update", null);
        }

        Map<String, String> updateFields = new HashMap<>();
        updateFields.put("state", State.COMPLETED.toString());
        updateFields.put("preference", Preference.LIKED.toString());
        updateFields.put("required", "true");

        return learnerRecordService.updateCourseRecordForLearner(learnerId, courseId, updateFields);
    }

    @PostMapping(path = "/module-record", produces = "application/json")
    public ResponseEntity<?> createModuleRecordForLearner(@Valid @RequestBody ModuleRecordInput moduleRecordInput,
                                                          Authentication authentication) {
        log.debug("courseId: {}", moduleRecordInput.getCourseId());
        String learnerId = getLearnerIdFromAuth(authentication);
        if(StringUtils.isBlank(learnerId)) {
            return returnError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Learner Id is missing from authentication token","/module-record", null);
        }
        moduleRecordInput.setUserId(learnerId);
        return learnerRecordService.createModuleRecordForLearner(moduleRecordInput);
    }

    @PostMapping(path = "/module-record/update/{moduleRecordId}", produces = "application/json")
    public ResponseEntity<?> updateModuleRecordForLearner(@PathVariable("moduleRecordId") Long moduleRecordId,
                                                          Authentication authentication) {
        log.debug("moduleRecordId: {}", moduleRecordId);
        String learnerId = getLearnerIdFromAuth(authentication);
        if(StringUtils.isBlank(learnerId)) {
            return returnError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Learner Id is missing from authentication token","/module-record/update", null);
        }

        Map<String, String> updateFields = new HashMap<>();
        updateFields.put("uid", UUID.randomUUID().toString());
        updateFields.put("state", State.COMPLETED.toString());
        updateFields.put("result", Result.PASSED.toString());
        updateFields.put("completionDate", LocalDateTime.now().toString());
        updateFields.put("score", "100");
        updateFields.put("rated", "true");
        updateFields.put("bookingStatus", BookingStatus.CONFIRMED.name());
        updateFields.put("eventDate", LocalDateTime.now().toString());

        return learnerRecordService.updateModuleRecordForLearner(moduleRecordId, updateFields);
    }

    //Only three inputs are required: registrationId, courseId and moduleId and
    // the fourth input learnerId will be retrieved from the authentication
    @PostMapping(path = "/launch-link", produces = "application/json")
    public ResponseEntity<?> getRegistrationLaunchLink(@Valid @RequestBody RegistrationInput registrationInput,
                                                       Authentication authentication) {
        log.debug("registrationId: {}", registrationInput.getRegistrationId());
        String learnerId = getLearnerIdFromAuth(authentication);
        registrationInput.setLearnerId(learnerId);
        if(StringUtils.isBlank(learnerId)) {
            return returnError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Learner Id is missing from authentication token","/launch-link", null);
        }
        return rusticiService.getRegistrationLaunchLink(registrationInput);
    }

    //Only four inputs are required: registrationId, courseId, moduleId and learnerFirstName and
    // the fifth input learnerId will be retrieved from the authentication
    @PostMapping(path = "/registration-launch-link", produces = "application/json")
    public ResponseEntity<?> createRegistrationAndLaunchLink(@Valid @RequestBody RegistrationInput registrationInput,
                                                             Authentication authentication) {
        log.debug("registrationId: {}", registrationInput.getRegistrationId());
        String learnerId = getLearnerIdFromAuth(authentication);
        registrationInput.setLearnerId(learnerId);
        if(StringUtils.isBlank(learnerId)) {
            return returnError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Learner Id is missing from authentication token","/registration-launch-link", null);
        }
        return rusticiService.createRegistrationAndLaunchLink(registrationInput);
    }
}
