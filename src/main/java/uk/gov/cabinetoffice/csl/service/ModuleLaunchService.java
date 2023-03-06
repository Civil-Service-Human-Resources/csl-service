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

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.*;

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
            //8. If no launch link present then create the registration and launch link using withLaunchLink
            registrationLaunchLinkResponse =
                    rusticiService.createRegistrationAndLaunchLink(registrationInput);
        }
        if(registrationLaunchLinkResponse.getStatusCode().is2xxSuccessful()) {
            log.info("Module launch link is successfully retrieved for learner id: {}, course id: "
                    + "{} and module id: {}", learnerId, courseId, moduleId);
            //9. Check and Update launchLink for disabledBookmarking
            registrationLaunchLinkResponse = checkAndSetDisabledBookMarking(moduleId, learnerId, courseId,
                    registrationLaunchLinkResponse);
            //10. Update the module record for the last updated timestamp
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
