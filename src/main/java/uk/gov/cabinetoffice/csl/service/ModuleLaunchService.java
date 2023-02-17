package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.returnError;

@Slf4j
@Service
public class ModuleLaunchService {

    private final LearnerRecordService learnerRecordService;

    private final RusticiService rusticiService;

    public ModuleLaunchService(LearnerRecordService learnerRecordService, RusticiService rusticiService) {
        this.learnerRecordService = learnerRecordService;
        this.rusticiService = rusticiService;
    }

    public ResponseEntity<?> createLaunchLink(CourseRecordInput courseRecordInput) {
        String learnerId = courseRecordInput.getUserId();
        String courseId = courseRecordInput.getCourseId();
        String moduleId = courseRecordInput.getModuleRecords().get(0).getModuleId();

        //1. Fetch the course record from the learner-record-service
        ResponseEntity<?> courseRecordForLearnerResponse = learnerRecordService.getCourseRecordForLearner(learnerId, courseId);
        if(courseRecordForLearnerResponse.getStatusCode().is2xxSuccessful()) {
            CourseRecords courseRecords = (CourseRecords)courseRecordForLearnerResponse.getBody();
            if(courseRecords != null) {
                CourseRecord courseRecord = courseRecords.getCourseRecord(courseId);

                //TODO: May need to check courseRecords.size() == 0 in below condition if courseRecord is not null but empty object
                if(courseRecord == null) {
                    //2. If the course record is not present then create the course record along with module record
                    courseRecordForLearnerResponse = learnerRecordService.createCourseRecordForLearner(courseRecordInput);
                    if(courseRecordForLearnerResponse.getStatusCode().is2xxSuccessful()) {
                        courseRecord = (CourseRecord)courseRecordForLearnerResponse.getBody();
                    }
                }

                if(courseRecord != null) {
                    //3. Retrieve the relevant module record from the course record
                    ModuleRecord moduleRecord = courseRecord.getModuleRecord(moduleId);

                    if(moduleRecord == null) {
                        //4. If the relevant module record is not present then create the module record
                        if(StringUtils.isBlank(courseRecordInput.getModuleRecords().get(0).getUid())){
                            courseRecordInput.getModuleRecords().get(0).setUid(UUID.randomUUID().toString());
                        }
                        ResponseEntity<?> moduleRecordForLearnerResponse = learnerRecordService.createModuleRecordForLearner(
                                courseRecordInput.getModuleRecords().get(0));

                        if(moduleRecordForLearnerResponse.getStatusCode().is2xxSuccessful()) {
                            moduleRecord = (ModuleRecord)moduleRecordForLearnerResponse.getBody();
                        }
                    }

                    if(moduleRecord != null) {
                        if(StringUtils.isBlank(moduleRecord.getUid())) {
                            //5. If the uid is not present in the module record then update the module record to assign the uid
                            Map<String, String> updateFields = new HashMap<>();
                            updateFields.put("uid", UUID.randomUUID().toString());
                            ResponseEntity<?> moduleRecordForLearnerResponse = learnerRecordService.updateModuleRecordForLearner(moduleRecord.getId(), updateFields);

                            if(moduleRecordForLearnerResponse.getStatusCode().is2xxSuccessful()) {
                                moduleRecord = (ModuleRecord)moduleRecordForLearnerResponse.getBody();
                            }
                        }

                        if(moduleRecord.getUid() != null) {
                            //6. Get the launchLink using module uid as registration id
                            RegistrationInput registrationInput = new RegistrationInput();
                            registrationInput.setRegistrationId(moduleRecord.getUid());
                            registrationInput.setLearnerId(learnerId);
                            registrationInput.setCourseId(courseId);
                            registrationInput.setModuleId(moduleId);
                            ResponseEntity<?> registrationLaunchLinkResponse = rusticiService.getRegistrationLaunchLink(registrationInput);
                            if (registrationLaunchLinkResponse.getStatusCode().is2xxSuccessful()) {
                                return registrationLaunchLinkResponse;
                            } else {
                                //TODO: Update firstName and lastName
                                registrationInput.setLearnerFirstName("firstName");
                                registrationInput.setLearnerLastName("lastName");
                                //7. If no launch link present then create the registration and launch link using withLaunchLink
                                return rusticiService.createRegistrationAndLaunchLink(registrationInput);
                            }
                        } else {
                            return returnError(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                    "Unable to update module record for the learnerId: " + learnerId + ", courseId: "
                                            + courseId + ", moduleId: " + moduleId,
                                    "/courses/" + courseId + "/modules/" +  moduleId + " /launch", null);
                        }
                    } else {
                        return returnError(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                "Unable to create module record for the learnerId: " + learnerId + ", courseId: "
                                        + courseId + ", moduleId: " + moduleId,
                                "/courses/" + courseId + "/modules/" +  moduleId + " /launch", null);
                    }
                } else {
                    return returnError(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            "Unable to create course record for the learnerId: " + learnerId + ", courseId: "
                                    + courseId + ", moduleId: " + moduleId,
                            "/courses/" + courseId + "/modules/" +  moduleId + " /launch", null);
                }
            } else {
                return returnError(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "Unable to retrieve course record for the learnerId: " + learnerId + ", courseId: "
                                + courseId, "/courses/" + courseId + "/modules/" +  moduleId + " /launch", null);
            }
        }

        return courseRecordForLearnerResponse;
    }
}
