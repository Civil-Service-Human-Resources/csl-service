package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.mapJsonStringToObject;

@Slf4j
@Service
public class ModuleRollupService {

    private final LearnerRecordService learnerRecordService;

    public ModuleRollupService(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    public CourseRecord processRusticiRollupData(RusticiRollupData rusticiRollupData) {
        log.debug("rusticiRollupData: {}", rusticiRollupData);
        String courseIdDotModuleId = rusticiRollupData.getCourse().getId();
        if(!courseIdDotModuleId.contains(".")) {
            log.error("Invalid rustici rollup data. \".\" is missing from course.id: {}", rusticiRollupData);
            return null;
        }
        String[] courseIdDotModuleIdParts = courseIdDotModuleId.split("\\.");
        String courseId = courseIdDotModuleIdParts[0];
        String moduleId = courseIdDotModuleIdParts[1];
        String learnerId = rusticiRollupData.getLearner().getId();
        LocalDateTime updated = rusticiRollupData.getUpdated();
        LocalDateTime completedDate = rusticiRollupData.getCompletedDate();
        String result = rusticiRollupData.getRegistrationSuccess();
        CourseRecord courseRecord = null;
        ModuleRecord moduleRecord = null;
        ResponseEntity<?> courseRecordResponse = learnerRecordService.getCourseRecordForLearner(learnerId, courseId);
        if(courseRecordResponse.getStatusCode().is2xxSuccessful()) {
            CourseRecords courseRecords =
                    mapJsonStringToObject((String) courseRecordResponse.getBody(), CourseRecords.class);
            log.debug("courseRecords: {}", courseRecords);
            if (courseRecords != null) {
                courseRecord = courseRecords.getCourseRecord(courseId);
                //TODO check if above courseRecord contains all the modules in it or not
                moduleRecord = courseRecord != null ? courseRecord.getModuleRecord(moduleId) : null;
                if(moduleRecord != null) {
                    Map<String, String> updateFields = new HashMap<>();
                    updateFields.put("updatedAt", updated.toString());
                    if(completedDate != null) {
                        updateFields.put("completionDate", completedDate.toString());
                        updateFields.put("state", State.COMPLETED.name()); //rusticiRollupData.getRegistrationCompletion()
                    }
                    if(isNotBlank(result)) {
                        updateFields.put("result", Result.valueOf(result).name());
                    }
                    moduleRecord = learnerRecordService.updateModuleRecord(moduleRecord.getId(), updateFields);
                    //TODO: check if the above moduleRecord holds the course record in it or not
                    if(moduleRecord != null) {
                        //TODO: Calculate and Update course completion status by calling learning-catalogue service
                        //updateCourseRecordState(learnerId, courseId, State state, updated)
                    }
                }
            }
        }
        if(courseRecord == null || moduleRecord == null) {
            log.error("Unable to process the rustici rollup data: {}", rusticiRollupData);
        }
        log.debug("courseRecord after processing rollup data: {}", courseRecord);
        log.debug("moduleRecord after processing rollup data: {}", moduleRecord);
        return courseRecord;
    }
}
