package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

import java.util.List;

@Service
@Slf4j
public class LearnerRecordUpdateProcessor {

    private final LearnerRecordService learnerRecordService;

    public LearnerRecordUpdateProcessor(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    public CourseRecord processCourseRecordAction(String learnerId, String courseId, ICourseRecordUpdate update) {
        log.info(String.format("Applying update '%s' to course record for course '%s' and user '%s'",
                update.getName(), courseId, learnerId));
        CourseRecord courseRecord = learnerRecordService.getCourseRecord(learnerId, courseId);
        if (courseRecord == null) {
            courseRecord = applyCreateUpdateToCourseRecord(learnerId, courseId, update);
        } else {
            courseRecord = applyPatchUpdateToCourseRecord(courseRecord, update);
        }
        return learnerRecordService.updateCourseRecordCache(courseRecord);
    }

    public CourseRecord processModuleRecordAction(String learnerId, String courseId, String moduleId, IModuleRecordUpdate update) {
        CourseRecord courseRecord = learnerRecordService.getCourseRecord(learnerId, courseId);
        if (courseRecord == null) {
            courseRecord = applyCreateUpdateToCourseRecord(learnerId, courseId, moduleId, update);
        } else {
            ModuleRecord moduleRecord = courseRecord.getModuleRecord(moduleId);
            if (moduleRecord == null) {
                moduleRecord = applyCreateUpdateToModuleRecord(courseRecord, moduleId, update);
            } else {
                moduleRecord = applyPatchUpdateToModuleRecord(moduleRecord, update);
            }
            courseRecord.updateModuleRecords(moduleRecord);
            courseRecord = applyPatchUpdateToCourseRecord(courseRecord, update);
        }
        return learnerRecordService.updateCourseRecordCache(courseRecord);
    }

    private CourseRecord applyCreateUpdateToCourseRecord(String learnerId, String courseId, ICourseRecordUpdate update) {
        CourseRecordStatus courseRecordStatus = update.getCreateCourseRecordStatus();
        return learnerRecordService.createCourseRecord(learnerId, courseId, courseRecordStatus);
    }

    private CourseRecord applyCreateUpdateToCourseRecord(String learnerId, String courseId, String moduleId, ICourseRecordUpdate update) {
        CourseRecordStatus courseRecordStatus = update.getCreateCourseRecordStatus();
        ModuleRecordStatus moduleRecordStatus = courseRecordStatus.getModuleRecordStatus();
        return learnerRecordService.createCourseRecord(learnerId, courseId, moduleId, courseRecordStatus, moduleRecordStatus);
    }

    private CourseRecord applyPatchUpdateToCourseRecord(CourseRecord courseRecord, ICourseRecordUpdate update) {
        List<PatchOp> courseRecordPatches = update.getUpdateCourseRecordPatches(courseRecord);
        if (!courseRecordPatches.isEmpty()) {
            courseRecord = learnerRecordService.updateCourseRecord(courseRecord.getUserId(), courseRecord.getCourseId(), courseRecordPatches);
        }
        return courseRecord;
    }

    private ModuleRecord applyCreateUpdateToModuleRecord(CourseRecord courseRecord, String moduleId, IModuleRecordUpdate update) {
        ModuleRecordStatus status = update.getCreateModuleRecordStatus();
        return learnerRecordService.createModuleRecord(courseRecord.getUserId(),
                courseRecord.getCourseId(), moduleId, status);
    }

    private ModuleRecord applyPatchUpdateToModuleRecord(ModuleRecord moduleRecord, IModuleRecordUpdate update) {
        List<PatchOp> moduleRecordPatches = update.getUpdateModuleRecordPatches(moduleRecord);
        if (!moduleRecordPatches.isEmpty()) {
            moduleRecord = learnerRecordService.updateModuleRecord(moduleRecord.getId(), moduleRecordPatches);
        }
        return moduleRecord;
    }
}
