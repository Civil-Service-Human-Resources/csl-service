package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.UUID.randomUUID;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.invokeService;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.mapJsonStringToObject;

@Slf4j
@Service
public class LearnerRecordService {

    private final RequestEntityWithBearerAuthFactory requestEntityFactory;

    @Value("${learnerRecord.courseRecordsForLearnerUrl}")
    private String courseRecordsForLearnerUrl;

    @Value("${learnerRecord.moduleRecordsForLearnerUrl}")
    private String moduleRecordsForLearnerUrl;

    public LearnerRecordService(RequestEntityWithBearerAuthFactory requestEntityFactory) {
        this.requestEntityFactory = requestEntityFactory;
    }

    public ResponseEntity<?> getCourseRecordForLearner(String learnerId, String courseId) {
        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createGetRequestWithBearerAuth(
                courseRecordsForLearnerUrl + "?userId=" + learnerId + "&courseId=" + courseId,
                null);
        return invokeService(requestWithBearerAuth);
    }

    public ResponseEntity<?> createCourseRecordForLearner(CourseRecordInput courseRecordInput) {
        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPostRequestWithBearerAuth(
                courseRecordsForLearnerUrl, courseRecordInput, null);
        return invokeService(requestWithBearerAuth);
    }

    public ResponseEntity<?> updateCourseRecordForLearner(String learnerId, String courseId,
                                                          Map<String, String> updateFields) {
        List<PatchOp> jsonPatch = new ArrayList<>();
        updateFields.forEach((key, value) -> jsonPatch.add(new PatchOp("replace", "/" + key, value)));

        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPatchRequestWithBearerAuth(
                courseRecordsForLearnerUrl + "?userId=" + learnerId + "&courseId=" + courseId,
                jsonPatch, null);
        return invokeService(requestWithBearerAuth);
    }

    public CourseRecord updateCourseRecordState(String learnerId, String courseId, State state, LocalDateTime updatedAt) {
        Map<String, String> updateFields = new HashMap<>();
        updateFields.put("state", state.name());
        updateFields.put("lastUpdated", updatedAt.toString());
        ResponseEntity<?> updateResponse = updateCourseRecordForLearner(learnerId, courseId, updateFields);
        if(updateResponse.getStatusCode().is2xxSuccessful()) {
            CourseRecord courseRecord =
                    mapJsonStringToObject((String)updateResponse.getBody(), CourseRecord.class);
            log.debug("courseRecord: {}", courseRecord);
            log.info("Course record status and lastUpdated are update for learner id: {}, course id: {} and state: {}",
                    learnerId, courseId, state);
            return courseRecord;
        }
        log.error("Unable to update course record status and lastUpdated for learner id: {}, course id: {} and state: {}",
                learnerId, courseId, state);
        return null;
    }

    public ResponseEntity<?> createModuleRecordForLearner(ModuleRecordInput moduleRecordInput) {
        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPostRequestWithBearerAuth(
                moduleRecordsForLearnerUrl, moduleRecordInput, null);
        return invokeService(requestWithBearerAuth);
    }

    public ResponseEntity<?> updateModuleRecordForLearner(Long moduleRecordId,
                                                          Map<String, String> updateFields) {
        List<PatchOp> jsonPatch = new ArrayList<>();
        updateFields.forEach((key, value) -> jsonPatch.add(new PatchOp("replace", "/" + key, value)));

        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPatchRequestWithBearerAuth(
                moduleRecordsForLearnerUrl + "/" + moduleRecordId, jsonPatch, null);
        return invokeService(requestWithBearerAuth);
    }

    public CourseRecord createInProgressCourseRecordWithModuleRecord(CourseRecordInput courseRecordInput) {
        courseRecordInput.setState(State.IN_PROGRESS.name());
        String learnerId = courseRecordInput.getUserId();
        String courseId = courseRecordInput.getCourseId();
        ModuleRecordInput moduleRecordInput = courseRecordInput.getModuleRecords().get(0);
        if(StringUtils.isBlank(moduleRecordInput.getUid())) {
            moduleRecordInput.setUid(randomUUID().toString());
        }
        moduleRecordInput.setState(State.IN_PROGRESS.name());
        ResponseEntity<?> courseRecordForLearnerResponse = createCourseRecordForLearner(courseRecordInput);
        if(courseRecordForLearnerResponse.getStatusCode().is2xxSuccessful()) {
            CourseRecord courseRecord =
                    mapJsonStringToObject((String)courseRecordForLearnerResponse.getBody(), CourseRecord.class);
            log.debug("courseRecord: {}", courseRecord);
            log.info("A new course record is created for learner id: {}, course id: {} and module id: {}",
                    learnerId, courseId, moduleRecordInput.getModuleId());
            return courseRecord;
        }
        log.error("Unable to create a new course record for learner id: {}, course id: {} and module id: {}. " +
                    "Error response from learnerRecordService: {}", learnerId, courseId,
                    moduleRecordInput.getModuleId(), courseRecordForLearnerResponse);
        return null;
    }

    public ModuleRecord createInProgressModuleRecord(ModuleRecordInput moduleRecordInput) {
        if(StringUtils.isBlank(moduleRecordInput.getUid())){
            moduleRecordInput.setUid(randomUUID().toString());
        }
        moduleRecordInput.setState(State.IN_PROGRESS.name());
        ResponseEntity<?> moduleRecordForLearnerResponse = createModuleRecordForLearner(moduleRecordInput);

        if(moduleRecordForLearnerResponse.getStatusCode().is2xxSuccessful()) {
            ModuleRecord moduleRecord =
                    mapJsonStringToObject((String)moduleRecordForLearnerResponse.getBody(), ModuleRecord.class);
            log.debug("moduleRecord: {}", moduleRecord);
            assert moduleRecord != null;
            log.info("A new module record is created for learner id: {}, course id: {} and module id: {}",
                    moduleRecordInput.getUserId(), moduleRecordInput.getCourseId(), moduleRecord.getModuleId());
            return moduleRecord;
        }
        log.error("Unable to create a new course record for learner id: {}, course id: {} and module id: {}. " +
                        "Error response from learnerRecordService: {}", moduleRecordInput.getUserId(),
                moduleRecordInput.getCourseId(), moduleRecordInput.getModuleId(), moduleRecordForLearnerResponse);
        return null;
    }

    public ModuleRecord updateModuleRecordToAssignUid(ModuleRecord moduleRecord, String learnerId, String courseId) {
        String moduleId = moduleRecord.getModuleId();
        String currentDateAndTime = LocalDateTime.now().toString();
        Map<String, String> updateFields = new HashMap<>();
        updateFields.put("updatedAt", currentDateAndTime);
        updateFields.put("uid", randomUUID().toString());
        ResponseEntity<?> updateResponse = updateModuleRecordForLearner(moduleRecord.getId(), updateFields);
        if(updateResponse.getStatusCode().is2xxSuccessful()) {
            moduleRecord = mapJsonStringToObject((String)updateResponse.getBody(), ModuleRecord.class);
            assert moduleRecord != null;
            log.info("uid and updatedAt fields are updated for the module record for learner id: "
                    + "{}, course id: {} and module id: {}", learnerId, courseId, moduleId);
        } else {
            log.error("Unable to update uid for the module record for learner id: {}, course id: {} and module id: {}."
                      + " Error response from learnerRecordService: {}", learnerId, courseId, moduleId, updateResponse);
        }
        log.debug("moduleRecord: {}", moduleRecord);
        return moduleRecord;
    }

    public ModuleRecord updateModuleUpdateDateTime(ModuleRecord moduleRecord, LocalDateTime updatedAt,
                                                   String learnerId, String courseId) {
        String moduleId = moduleRecord.getModuleId();
        Map<String, String> updateFields = new HashMap<>();
        updateFields.put("updatedAt", updatedAt.toString());
        ResponseEntity<?> updateDateTimeResponse = updateModuleRecordForLearner(moduleRecord.getId(), updateFields);
        if(updateDateTimeResponse.getStatusCode().is2xxSuccessful()) {
            moduleRecord = mapJsonStringToObject((String)updateDateTimeResponse.getBody(), ModuleRecord.class);
            log.debug("moduleRecord: {}", moduleRecord);
            log.info("updatedAt field is updated for the module record for learner id: {}, course id: {} and "
                    + "module id: {}", learnerId, courseId, moduleId);
        } else {
            log.error("Unable to update updatedAt for the module record for learner id: {}, course id: {} and "
                    + "module id: {} due to {}", learnerId, courseId, moduleId, updateDateTimeResponse);
        }
        return moduleRecord;
    }
}
