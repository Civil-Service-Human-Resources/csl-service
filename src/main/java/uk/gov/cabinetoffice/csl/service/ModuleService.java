package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordActionProcessor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ModuleRecordActionService;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ModuleRecordUpdate;
import uk.gov.cabinetoffice.csl.domain.rustici.ModuleLaunchLinkInput;

@Service
@Slf4j
public class ModuleService {

    private final LearnerRecordActionProcessor learnerRecordActionProcessor;
    private final LearnerRecordService learnerRecordService;
    private final ModuleRecordActionService moduleRecordActionService;

    public ModuleService(LearnerRecordActionProcessor learnerRecordActionProcessor, LearnerRecordService learnerRecordService,
                         ModuleRecordActionService moduleRecordActionService) {
        this.learnerRecordActionProcessor = learnerRecordActionProcessor;
        this.learnerRecordService = learnerRecordService;
        this.moduleRecordActionService = moduleRecordActionService;
    }

    public ModuleRecord launchModule(String learnerId, String courseId, String moduleId,
                                     ModuleLaunchLinkInput moduleLaunchLinkInput) {
        ModuleRecordUpdate update = moduleRecordActionService.getLaunchModuleUpdate(moduleLaunchLinkInput.getCourseIsRequired());
        CourseRecord courseRecord = processModuleRecordAction(learnerId, courseId, moduleId, update);
        return courseRecord.getModuleRecord(moduleId);
    }

    private CourseRecord processModuleRecordAction(String learnerId, String courseId, String moduleId, ModuleRecordUpdate update) {
        CourseRecord courseRecord = learnerRecordService.getCourseRecord(learnerId, courseId);
        if (courseRecord == null) {
            courseRecord = learnerRecordActionProcessor.applyCreateUpdateToCourseRecord(learnerId, courseId, moduleId, update);
        } else {
            ModuleRecord moduleRecord = courseRecord.getModuleRecord(moduleId);
            if (moduleRecord == null) {
                moduleRecord = learnerRecordActionProcessor.applyCreateUpdateToModuleRecord(courseRecord, moduleId, update);
            } else {
                moduleRecord = learnerRecordActionProcessor.applyPatchUpdateToModuleRecord(moduleRecord, update);
            }
            courseRecord.updateModuleRecords(moduleRecord);
            courseRecord = learnerRecordActionProcessor.applyPatchUpdateToCourseRecord(courseRecord, update);
        }
        return courseRecord;
    }

}
