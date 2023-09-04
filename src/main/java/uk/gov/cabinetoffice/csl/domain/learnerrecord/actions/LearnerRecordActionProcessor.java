package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

import java.util.List;

@Service
public class LearnerRecordActionProcessor {

    private final LearnerRecordService learnerRecordService;

    public LearnerRecordActionProcessor(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    public CourseRecord applyCreateUpdateToCourseRecord(String learnerId, String courseId, CourseRecordUpdate update) {
        CourseRecordStatus courseRecordStatus = update.getCreateCourseRecordStatus();
        return learnerRecordService.createCourseRecord(learnerId, courseId, courseRecordStatus);
    }

    public CourseRecord applyCreateUpdateToCourseRecord(String learnerId, String courseId, String moduleId, CourseRecordUpdate update) {
        CourseRecordStatus courseRecordStatus = update.getCreateCourseRecordStatus();
        ModuleRecordStatus moduleRecordStatus = courseRecordStatus.getModuleRecordStatus();
        return learnerRecordService.createCourseRecord(learnerId, courseId, moduleId, courseRecordStatus, moduleRecordStatus);
    }

    public CourseRecord applyPatchUpdateToCourseRecord(CourseRecord courseRecord, CourseRecordUpdate update) {
        List<PatchOp> courseRecordPatches = update.getUpdateCourseRecordPatches(courseRecord);
        if (!courseRecordPatches.isEmpty()) {
            courseRecord = learnerRecordService.updateCourseRecord(courseRecord.getUserId(), courseRecord.getCourseId(), courseRecordPatches);
        }
        return courseRecord;
    }

    public ModuleRecord applyCreateUpdateToModuleRecord(CourseRecord courseRecord, String moduleId, ModuleRecordUpdate update) {
        ModuleRecordStatus status = update.getCreateModuleRecordStatus();
        return learnerRecordService.createModuleRecord(courseRecord.getUserId(),
                courseRecord.getCourseId(), moduleId, status);
    }

    public ModuleRecord applyPatchUpdateToModuleRecord(ModuleRecord moduleRecord, ModuleRecordUpdate update) {
        List<PatchOp> moduleRecordPatches = update.getUpdateModuleRecordPatches(moduleRecord);
        if (!moduleRecordPatches.isEmpty()) {
            moduleRecord = learnerRecordService.updateModuleRecord(moduleRecord.getId(), moduleRecordPatches);
        }
        return moduleRecord;
    }
}
