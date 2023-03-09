package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
        String completion = rusticiRollupData.getRegistrationCompletion();
        String result = rusticiRollupData.getRegistrationSuccess();

        CourseRecord courseRecord = null;
        ResponseEntity<?> courseRecordResponse = learnerRecordService.getCourseRecordForLearner(learnerId, courseId);
        if(courseRecordResponse.getStatusCode().is2xxSuccessful()) {
            CourseRecords courseRecords =
                    mapJsonStringToObject((String) courseRecordResponse.getBody(), CourseRecords.class);
            log.debug("courseRecords: {}", courseRecords);
            if (courseRecords != null) {
                courseRecord = courseRecords.getCourseRecord(courseId);
                ModuleRecord moduleRecord = courseRecord != null ? courseRecord.getModuleRecord(moduleId) : null;
                if(moduleRecord != null) {
                    Map<String, String> updateFields = new HashMap<>();

                    //TODO: Update module data
                    //learnerRecordService.updateModuleRecordForLearner(moduleRecordId, Map<String, String> updateFields)

                    //TODO: Update course status
                    //updateCourseRecordState(learnerId, courseId, State state, LocalDateTime updatedAt)

//                    moduleRecord = learnerRecordService.updateModuleUpdateDateTime(moduleRecord, updated, learnerId, courseId);

                }
            }
        }

        if(courseRecord == null) {
            log.error("Unable to process the rustici rollup data: {}", rusticiRollupData);
        }
        log.debug("courseRecord after processing rollup data: {}", courseRecord);

        return courseRecord;
    }
}
