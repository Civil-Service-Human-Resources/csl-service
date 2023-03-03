package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.*;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.mapJsonStringToObject;

@Slf4j
@Service
public class ModuleRollupService {

    private final LearnerRecordService learnerRecordService;

    public ModuleRollupService(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    public void processRusticiRollupData(RusticiRollupData rusticiRollupData) {
        log.debug("rusticiRollupData: {}", rusticiRollupData);
        String courseIdDotModuleId = rusticiRollupData.getCourse().getId();
        if(!courseIdDotModuleId.contains(".")) {
            log.error("Invalid rustici rollup data. \".\" is missing from course.id: {}", rusticiRollupData);
            return;
        }
        //1. Get the courseId, moduleId and learnerId from the rollup data
        String[] courseIdDotModuleIdParts = courseIdDotModuleId.split("\\.");
        String courseId = courseIdDotModuleIdParts[0];
        String moduleId = courseIdDotModuleIdParts[1];
        String learnerId = rusticiRollupData.getLearner().getId();
        //2. Fetch the course record from the learner-record-service
        ResponseEntity<?> courseRecordResponse = learnerRecordService.getCourseRecordForLearner(learnerId, courseId);
        if(courseRecordResponse.getStatusCode().is2xxSuccessful()) {
            CourseRecords courseRecords =
                    mapJsonStringToObject((String) courseRecordResponse.getBody(), CourseRecords.class);
            log.debug("courseRecords: {}", courseRecords);
            if (courseRecords != null) {
                CourseRecord courseRecord = courseRecords.getCourseRecord(courseId);
                if (courseRecord != null) {
                    //3. Retrieve the relevant module record from the course record
                    ModuleRecord moduleRecord = courseRecord.getModuleRecord(moduleId);
                    if(moduleRecord != null) {
                        if (StringUtils.isBlank(moduleRecord.getUid())) {
                            //4. If the uid is not present then update the module record to assign the uid
                            moduleRecord = learnerRecordService
                                    .updateModuleRecordToAssignUid(moduleRecord, learnerId, courseId);
                        }
                        //5. Update the module record for the last updated timestamp
                        learnerRecordService.updateModuleUpdateDateTime(moduleRecord, learnerId, courseId);
                        if (courseRecord.getState() == null || courseRecord.getState().equals(State.ARCHIVED)) {
                            //6. Update the course record status if it is null or ARCHIVED
                            learnerRecordService.updateCourseRecordState(learnerId, courseId, State.IN_PROGRESS);
                        }
                    }
                }
            }
        }
    }
}
