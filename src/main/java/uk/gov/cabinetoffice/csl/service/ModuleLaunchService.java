package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.mapJsonStringToObject;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.returnError;

@Slf4j
@Service
public class ModuleLaunchService {

    private final LearnerRecordService learnerRecordService;

    private final RusticiService rusticiService;

    private final String[] disabledBookmarkingModuleIDs;

    public ModuleLaunchService(LearnerRecordService learnerRecordService, RusticiService rusticiService,
        @Value("${rustici.disabledBookmarkingModuleIDs}") String[] disabledBookmarkingModuleIDs) {
        this.learnerRecordService = learnerRecordService;
        this.rusticiService = rusticiService;
        this.disabledBookmarkingModuleIDs = disabledBookmarkingModuleIDs;
    }

    public ResponseEntity<?> createLaunchLink(ModuleLaunchLinkInput moduleLaunchLinkInput) {
        log.debug("moduleLaunchLinkInput: {}", moduleLaunchLinkInput);
        CourseRecordInput courseRecordInput = moduleLaunchLinkInput.getCourseRecordInput();
        String learnerId = courseRecordInput.getUserId();
        String courseId = courseRecordInput.getCourseId();
        String moduleId = courseRecordInput.getModuleRecords().get(0).getModuleId();
        ModuleRecord moduleRecord = processCourseAndModuleData(learnerRecordService, courseRecordInput);
        if(moduleRecord != null && StringUtils.isNotBlank(moduleRecord.getUid())) {
            return createLaunchLink(moduleRecord, moduleLaunchLinkInput);
        }
        log.error("Unable to retrieve module launch link for the learnerId: {}, courseId: {} and moduleId: {}",
                learnerId, courseId, moduleId);
        return returnError(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Unable to retrieve module launch link for the learnerId: " + learnerId + ", courseId: "
                        + courseId + " and moduleId: " +  moduleId, "/courses/" + courseId + "/modules/"
                        +  moduleId + "/launch", null);
    }

    private ModuleRecord processCourseAndModuleData(LearnerRecordService learnerRecordService,
                                                          CourseRecordInput courseRecordInput) {
        ModuleRecord moduleRecord = null;
        String learnerId = courseRecordInput.getUserId();
        String courseId = courseRecordInput.getCourseId();
        ModuleRecordInput moduleRecordInput = courseRecordInput.getModuleRecords().get(0);
        String moduleId = moduleRecordInput.getModuleId();
        ResponseEntity<?> courseRecordResponse = learnerRecordService.getCourseRecordForLearner(learnerId, courseId);
        if(courseRecordResponse.getStatusCode().is2xxSuccessful()) {
            CourseRecords courseRecords =
                    mapJsonStringToObject((String)courseRecordResponse.getBody(), CourseRecords.class);
            log.debug("courseRecords: {}", courseRecords);
            if(courseRecords != null) {
                CourseRecord courseRecord = courseRecords.getCourseRecord(courseId);
                if(courseRecord == null) {
                    //If the course record is not present then create the course record along with module record
                    courseRecord = learnerRecordService.createInProgressCourseRecordWithModuleRecord(courseRecordInput);
                }
                if(courseRecord != null) {
                    if(courseRecord.getState() == null || courseRecord.getState().equals(State.ARCHIVED)) {
                        //Update the course record status if it is null or ARCHIVED
                        courseRecord = learnerRecordService.updateCourseRecordState(learnerId, courseId,
                                State.IN_PROGRESS, LocalDateTime.now());
                    }
                    //Retrieve the relevant module record from the course record
                    moduleRecord = courseRecord != null ? courseRecord.getModuleRecord(moduleId) : null;
                    if(courseRecord != null && moduleRecord == null) {
                        //If the relevant module record is not present then create the module record
                        moduleRecord = learnerRecordService.createInProgressModuleRecord(moduleRecordInput);
                    }
                    if(moduleRecord != null) {
                        if(StringUtils.isBlank(moduleRecord.getUid())) {
                            //If the uid is not present then update the module record to assign the uid
                            moduleRecord = learnerRecordService
                                    .updateModuleRecordToAssignUid(moduleRecord, learnerId, courseId);
                        }
                    }
                }
            }
        } else {
            log.error("Unable to retrieve course record for learner id: {} and course id: {}. " +
                    "Error response from learnerRecordService: {}", learnerId, courseId, courseRecordResponse);
        }
        return moduleRecord;
    }

    private ResponseEntity<?> createLaunchLink(ModuleRecord moduleRecord, ModuleLaunchLinkInput moduleLaunchLinkInput) {
        String learnerFirstName = moduleLaunchLinkInput.getLearnerFirstName();
        String learnerLastName = moduleLaunchLinkInput.getLearnerLastName();
        CourseRecordInput courseRecordInput = moduleLaunchLinkInput.getCourseRecordInput();
        String learnerId = courseRecordInput.getUserId();
        String courseId = courseRecordInput.getCourseId();
        String moduleId = moduleRecord.getModuleId();

        RegistrationInput registrationInput = new RegistrationInput();
        registrationInput.setRegistrationId(moduleRecord.getUid());
        registrationInput.setLearnerId(learnerId);
        registrationInput.setCourseId(courseId);
        registrationInput.setModuleId(moduleId);
        registrationInput.setLearnerFirstName(learnerFirstName);
        registrationInput.setLearnerLastName(learnerLastName == null ? "" : learnerLastName);

        ResponseEntity<?> registrationLaunchLinkResponse =
                rusticiService.getRegistrationLaunchLink(registrationInput);
        if(!registrationLaunchLinkResponse.getStatusCode().is2xxSuccessful()) {
            log.error("Module launch link could not be retrieved using launchLink endpoint for learner id: {}, " +
                      "course id: {} and module id: {} due to {}. Now invoking withLaunchLink endpoint to retrieve " +
                      "module launch link.", learnerId, courseId, moduleId, registrationLaunchLinkResponse);
            //If no launch link present then create the registration and launch link using withLaunchLink
            registrationLaunchLinkResponse = rusticiService.createRegistrationAndLaunchLink(registrationInput);
        }
        if(registrationLaunchLinkResponse.getStatusCode().is2xxSuccessful()) {
            log.info("Module launch link is successfully retrieved for learner id: {}, course id: "
                    + "{} and module id: {}", learnerId, courseId, moduleId);
            //Check and Update launchLink for disabledBookmarking
            registrationLaunchLinkResponse = checkAndSetDisabledBookMarking(moduleId, learnerId, courseId,
                    registrationLaunchLinkResponse);
            //Update the module record for the last updated timestamp
            learnerRecordService.updateModuleUpdateDateTime(moduleRecord, LocalDateTime.now(), learnerId, courseId);
        } else {
            log.error("Module launch link could not be retrieved using withLaunchLink endpoint for " +
                      "learner id: {}, course id: {} and module id: {} due to {}",
                      learnerId, courseId, moduleId, registrationLaunchLinkResponse);
        }
        return registrationLaunchLinkResponse;
    }

    private ResponseEntity<?> checkAndSetDisabledBookMarking(String moduleId, String learnerId, String courseId,
                                                             ResponseEntity<?> registrationLaunchLinkResponse) {
        if(isDisabledBookmarkingModuleID(moduleId)) {
            LaunchLink launchLink =
                    mapJsonStringToObject((String)registrationLaunchLinkResponse.getBody(), LaunchLink.class);
            if(launchLink != null) {
                String launchLinkWithDisabledBookmarking = launchLink.getLaunchLink()
                        + "&clearbookmark=true";
                launchLink.setLaunchLink(launchLinkWithDisabledBookmarking);
                log.info("Module launch link is updated for clearbookmark=true for learner id: "
                        + "{}, course id: {} and module id: {}", learnerId, courseId, moduleId);
                return new ResponseEntity<>(launchLink, HttpStatus.OK);
            }
        }
        return registrationLaunchLinkResponse;
    }

    private boolean isDisabledBookmarkingModuleID(String moduleId) {
        return Arrays.stream(disabledBookmarkingModuleIDs).anyMatch(moduleId::equalsIgnoreCase);
    }
}
