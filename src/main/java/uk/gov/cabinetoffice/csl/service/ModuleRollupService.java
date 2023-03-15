package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;

import java.time.LocalDateTime;
import java.util.*;

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
        log.info("rusticiRollupData: {}", rusticiRollupData);
        String courseIdDotModuleId = rusticiRollupData.getCourse().getId();
        if(!courseIdDotModuleId.contains(".")) {
            log.error("Invalid rustici rollup data. \".\" is missing from course.id: {}", rusticiRollupData);
            return null;
        }
        String[] courseIdDotModuleIdParts = courseIdDotModuleId.split("\\.");
        String courseId = courseIdDotModuleIdParts[0];
        String moduleId = courseIdDotModuleIdParts[1];
        String learnerId = rusticiRollupData.getLearner().getId();
        LocalDateTime completedDate = rusticiRollupData.getCompletedDate();
        LocalDateTime updated = rusticiRollupData.getUpdated();
        CourseRecord courseRecord = null;
        ModuleRecord moduleRecord = null;
        ResponseEntity<?> courseRecordResponse = learnerRecordService.getCourseRecordForLearner(learnerId, courseId);
        if(courseRecordResponse.getStatusCode().is2xxSuccessful()) {
            CourseRecords courseRecords =
                    mapJsonStringToObject((String) courseRecordResponse.getBody(), CourseRecords.class);
            log.debug("courseRecords: {}", courseRecords);
            courseRecord = courseRecords != null ? courseRecords.getCourseRecord(courseId) : null;
            moduleRecord = courseRecord != null ? courseRecord.getModuleRecord(moduleId) : null;
            moduleRecord = moduleRecord != null ? updateModuleRecord(moduleRecord, rusticiRollupData) : null;
            if(moduleRecord != null) {
                courseRecord.updateModuleRecords(moduleRecord);
                courseRecord = completedDate != null ?
                        updateCourseCompletionStatus(courseRecord, updated): courseRecord;
            }
        }
        if(courseRecord == null || moduleRecord == null) {
            log.error("Unable to process the rustici rollup data: {}", rusticiRollupData);
        }
        log.debug("courseRecord after processing rollup data: {}", courseRecord);
        log.debug("moduleRecord after processing rollup data: {}", moduleRecord);
        return courseRecord;
    }

    private ModuleRecord updateModuleRecord(ModuleRecord moduleRecord, RusticiRollupData rusticiRollupData) {
        LocalDateTime updated = rusticiRollupData.getUpdated();
        LocalDateTime completedDate = rusticiRollupData.getCompletedDate();
        String result = rusticiRollupData.getRegistrationSuccess();
        Map<String, String> updateFields = new HashMap<>();
        updateFields.put("updatedAt", updated.toString());
        if(completedDate != null) {
            updateFields.put("state", State.COMPLETED.name());
            updateFields.put("completionDate", completedDate.toString());
        }
        if(isNotBlank(result) && Arrays.stream(Result.values()).anyMatch(v -> v.name().equals(result))) {
            updateFields.put("result", result);
        }
        return learnerRecordService.updateModuleRecord(moduleRecord.getId(), updateFields);
    }

    private CourseRecord updateCourseCompletionStatus(CourseRecord courseRecord, LocalDateTime completedDate) {
        String courseId = courseRecord.getCourseId();
        String learnerId = courseRecord.getUserId();
        List<String> completedModuleIds = courseRecord.getModuleRecords().stream()
                .map(ModuleRecord::getModuleId).toList();
        Course catalogueCourse = learningCatalogueService.getCachedCourse(courseId);
        log.debug("catalogueCourse: {}", catalogueCourse);
        if (catalogueCourse == null) {
            learningCatalogueService.removeCourseFromCache(courseId);
            catalogueCourse = learningCatalogueService.getCachedCourse(courseId);
            log.debug("catalogueCourse: {}", catalogueCourse);
        }
        if (catalogueCourse != null) {
            List<String> difference;
            List<String> mandatoryModulesIds = catalogueCourse.getModules().stream()
                    .filter(m -> !m.isOptional()).map(Module::getId).toList();
            if (mandatoryModulesIds.size() > 0) {
                difference = findDifference(mandatoryModulesIds, completedModuleIds);
            } else {
                List<String> optionalModulesIds = catalogueCourse.getModules().stream()
                        .filter(Module::isOptional).map(Module::getId).toList();
                difference = findDifference(optionalModulesIds, completedModuleIds);
            }
            if (difference.size() == 0) {
                courseRecord = learnerRecordService.updateCourseRecordState(learnerId, courseId,
                        State.COMPLETED, completedDate);
            }
        }
        return courseRecord;
    }

    private static <T> List<T> findDifference(List<T> first, List<T> second)
    {
        List<T> diff = new ArrayList<>(first);
        diff.removeAll(second);
        return diff;
    }
}
