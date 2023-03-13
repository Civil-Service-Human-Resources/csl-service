package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.mapJsonStringToObject;

@Slf4j
@Service
public class ModuleRollupService {

    private final LearnerRecordService learnerRecordService;

    private final LearningCatalogueService learningCatalogueService;

    public ModuleRollupService(LearnerRecordService learnerRecordService,
                               LearningCatalogueService learningCatalogueService) {
        this.learnerRecordService = learnerRecordService;
        this.learningCatalogueService = learningCatalogueService;
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
                moduleRecord = courseRecord != null ? courseRecord.getModuleRecord(moduleId) : null;
                if(moduleRecord != null) {
                    Map<String, String> updateFields = new HashMap<>();
                    updateFields.put("updatedAt", updated.toString());
                    if(completedDate != null) {
                        updateFields.put("state", State.COMPLETED.name());
                        updateFields.put("completionDate", completedDate.toString());
                    }
                    if(isNotBlank(result) && Arrays.stream(Result.values()).anyMatch(v -> v.name().equals(result))) {
                        updateFields.put("result", result);
                    }
                    moduleRecord = learnerRecordService.updateModuleRecord(moduleRecord.getId(), updateFields);
                    if(moduleRecord != null) {
                        //TODO: Code within this if can be moved to a private method
                        //Update the courseRecord with the above updated moduleRecord
                        courseRecord.updateModuleRecords(moduleRecord);
                        Course catalogueCourse = learningCatalogueService.getCachedCourse(courseId);
                        log.debug("catalogueCourse: {}", catalogueCourse);
                        if(catalogueCourse == null) {
                            learningCatalogueService.removeCourseFromCache(courseId);
                            catalogueCourse = learningCatalogueService.getCachedCourse(courseId);
                            log.debug("catalogueCourse: {}", catalogueCourse);
                        }
                        List<String> mandatoryModulesIds = catalogueCourse.getModules().stream()
                                .filter(m -> !m.isOptional()).map(Module::getId).toList();
                        if(mandatoryModulesIds.size() > 0) {
                            //TODO: check if all the mandatory modules are present in the courseRecord.getModuleRecords()
                            //if yes then set then update the course status to completed and completed date
                            //updateCourseRecordState(learnerId, courseId, State state, updated)
                        } else {
                            //Get the optional modules Ids from
                            List<String> optionalModulesIds = catalogueCourse.getModules().stream()
                                    .filter(Module::isOptional).map(Module::getId).toList();
                            //TODO: check if all the optional modules are present in the courseRecord.getModuleRecords()
                            //if yes then set then update the course status to completed and completed date
                            //updateCourseRecordState(learnerId, courseId, State state, updated)
                        }
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
