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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        ModuleRecordInput moduleRecordInput = courseRecordInput.getModuleRecords().get(0);
        String moduleId = moduleRecordInput.getModuleId();
        //1. Fetch the course record from the learner-record-service
        ResponseEntity<?> courseRecordForLearnerResponse =
                learnerRecordService.getCourseRecordForLearner(learnerId, courseId);
        if(courseRecordForLearnerResponse.getStatusCode().is2xxSuccessful()) {
            CourseRecords courseRecords = (CourseRecords)courseRecordForLearnerResponse.getBody();
            log.debug("courseRecords: {}", courseRecords);
            if(courseRecords != null) {
                CourseRecord courseRecord = courseRecords.getCourseRecord(courseId);
                if(courseRecord == null) {
                    //2. If the course record is not present then create the course record along with module record
                    courseRecord = createCourseRecord(courseRecordInput);
                }
                if(courseRecord != null) {
                    //3. Retrieve the relevant module record from the course record
                    ModuleRecord moduleRecord = courseRecord.getModuleRecord(moduleId);
                    if(moduleRecord == null) {
                        //4. If the relevant module record is not present then create the module record
                        moduleRecord = createModuleRecord(moduleRecordInput);
                    }
                    if(moduleRecord != null) {
                        if(StringUtils.isBlank(moduleRecord.getUid())) {
                            //5. If the uid is not present in the module record then update the module record to assign
                            // the uid
                            moduleRecord = updateModuleRecordToAssignUid(moduleRecord, learnerId, courseId);
                        }
                        if(moduleRecord != null && StringUtils.isNotBlank(moduleRecord.getUid())) {
                            //6. Get the launchLink using module uid as registration id
                            return createLaunchLink(moduleRecord, moduleLaunchLinkInput);
                        }
                    }
                }
            }
        }
        log.error("Unable to retrieve module launch link for the learnerId: " + learnerId + ", courseId: " + courseId
                + ", moduleId: " +  moduleId);
        return returnError(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Unable to retrieve module launch link for the learnerId: " + learnerId + ", courseId: "
                        + courseId + ", moduleId: " +  moduleId, "/courses/" + courseId + "/modules/" +  moduleId
                        + "/launch", null);
    }

    private CourseRecord createCourseRecord(CourseRecordInput courseRecordInput) {
        courseRecordInput.setState(State.IN_PROGRESS.name());
        String learnerId = courseRecordInput.getUserId();
        String courseId = courseRecordInput.getCourseId();
        ModuleRecordInput moduleRecordInput = courseRecordInput.getModuleRecords().get(0);
        if(StringUtils.isBlank(moduleRecordInput.getUid())) {
            moduleRecordInput.setUid(UUID.randomUUID().toString());
        }
        moduleRecordInput.setState(State.IN_PROGRESS.name());
        ResponseEntity<?> courseRecordForLearnerResponse =
                learnerRecordService.createCourseRecordForLearner(courseRecordInput);
        if(courseRecordForLearnerResponse.getStatusCode().is2xxSuccessful()) {
            CourseRecord courseRecord = (CourseRecord)courseRecordForLearnerResponse.getBody();
            log.debug("courseRecord: {}", courseRecord);
            log.info("A new course record is created for learner id: {} and course id: {}",
                    learnerId, courseId);
            return courseRecord;
        }
        log.error("Unable to create a new course record learner id: {} and course id: {}", learnerId, courseId);
        return null;
    }

    private ModuleRecord createModuleRecord(ModuleRecordInput moduleRecordInput) {
        if(StringUtils.isBlank(moduleRecordInput.getUid())){
            moduleRecordInput.setUid(UUID.randomUUID().toString());
        }
        moduleRecordInput.setState(State.IN_PROGRESS.name());
        ResponseEntity<?> moduleRecordForLearnerResponse =
                learnerRecordService.createModuleRecordForLearner(
                        moduleRecordInput);

        if(moduleRecordForLearnerResponse.getStatusCode().is2xxSuccessful()) {
            ModuleRecord moduleRecord = (ModuleRecord)moduleRecordForLearnerResponse.getBody();
            log.debug("moduleRecord: {}", moduleRecord);
            assert moduleRecord != null;
            log.info("A new module record is created for learner id: {}, course id: {} and module id: "
                    + "{}", moduleRecordInput.getUserId(), moduleRecordInput.getCourseId(),
                    moduleRecord.getModuleId());
            return moduleRecord;
        }
        log.error("Unable to create a new module record learner id: {} and course id: {}",
                    moduleRecordInput.getUserId(), moduleRecordInput.getCourseId());
        return null;
    }

    private ModuleRecord updateModuleRecordToAssignUid(ModuleRecord moduleRecord, String learnerId, String courseId) {
        String currentDateAndTime = LocalDateTime.now().toString();
        Map<String, String> updateFields = new HashMap<>();
        updateFields.put("updatedAt", currentDateAndTime);
        updateFields.put("uid", UUID.randomUUID().toString());
        ResponseEntity<?> updateFieldsResponse =
                learnerRecordService.updateModuleRecordForLearner(moduleRecord.getId(), updateFields);

        if(updateFieldsResponse.getStatusCode().is2xxSuccessful()) {
            moduleRecord = (ModuleRecord)updateFieldsResponse.getBody();
            assert moduleRecord != null;
            log.info("uid and updatedAt fields are updated for the module record for learner id: "
                    + "{}, course id: {} and module id: {}", learnerId, courseId, moduleRecord.getModuleId());
        } else {
            log.error("Unable to update uid for the module record for learner id: {}, course id: {} and module id: {}",
                    learnerId, courseId, moduleRecord.getModuleId());
        }
        log.debug("moduleRecord: {}", moduleRecord);
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
            log.warn("Module launch link could not be retrieved using launchLink endpoint, now invoking withLaunchLink"
                    + "endpoint to retrieve module launch link for learner id: {}, course id: {} and module id: {}",
                    learnerId, courseId, moduleId);
            //7. If no launch link present then create the registration and launch link using withLaunchLink
            registrationLaunchLinkResponse =
                    rusticiService.createRegistrationAndLaunchLink(registrationInput);
        }
        if(registrationLaunchLinkResponse.getStatusCode().is2xxSuccessful()) {
            log.info("Module launch link is successfully retrieved for learner id: {}, course id: "
                    + "{} and module id: {}", learnerId, courseId, moduleId);
            //8. Check and Update launchLink for disabledBookmarking
            registrationLaunchLinkResponse = checkAndSetDisabledBookMarking(moduleId, learnerId, courseId,
                    registrationLaunchLinkResponse);
            //9. Update the module record for the last updated timestamp
            updateModuleUpdateDateTime(moduleRecord.getId(), learnerId, courseId);
        } else {
            log.error("Module launch link could not be retrieved using withLaunchLink endpoint for " +
                    "learner id: {}, course id: {} and module id: {}", learnerId, courseId, moduleId);
        }
        return registrationLaunchLinkResponse;
    }

    private ResponseEntity<?> checkAndSetDisabledBookMarking(String moduleId, String learnerId, String courseId,
                                                             ResponseEntity<?> registrationLaunchLinkResponse) {
        if(isDisabledBookmarkingModuleID(moduleId)) {
            LaunchLink launchLink = (LaunchLink) registrationLaunchLinkResponse.getBody();
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

    private void updateModuleUpdateDateTime(Long id, String learnerId, String courseId){
        String currentDateAndTime = LocalDateTime.now().toString();
        Map<String, String> updateDateTimeMap = new HashMap<>();
        updateDateTimeMap.put("updatedAt", currentDateAndTime);
        ResponseEntity<?> updateDateTimeResponse =
                learnerRecordService.updateModuleRecordForLearner(id,
                        updateDateTimeMap);
        if(updateDateTimeResponse.getStatusCode().is2xxSuccessful()) {
            ModuleRecord moduleRecord = (ModuleRecord)updateDateTimeResponse.getBody();
            log.debug("moduleRecord: {}", moduleRecord);
            assert moduleRecord != null;
            log.info("updatedAt field is updated for the module record after retrieving the module launch link for" +
                            " learner id: {}, course id: {} and module id: {}",
                    learnerId, courseId, moduleRecord.getModuleId());
        } else {
            log.error("Unable to update updatedAt for the module record for learner id: {}, course id: {} and " +
                    "module Long DB id: {}", learnerId, courseId, id);
        }
    }
}
