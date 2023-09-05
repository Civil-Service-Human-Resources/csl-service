package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.ModuleResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordActionProcessor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ModuleRecordActionService;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ModuleRecordUpdate;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.rustici.ModuleLaunchLinkInput;

import java.util.List;

@Service
@Slf4j
public class ModuleService {

    private final LearnerRecordActionProcessor learnerRecordActionProcessor;
    private final LearningCatalogueService learningCatalogueService;
    private final LearnerRecordService learnerRecordService;
    private final ModuleRecordActionService moduleRecordActionService;

    public ModuleService(LearnerRecordActionProcessor learnerRecordActionProcessor, LearningCatalogueService learningCatalogueService,
                         LearnerRecordService learnerRecordService, ModuleRecordActionService moduleRecordActionService) {
        this.learnerRecordActionProcessor = learnerRecordActionProcessor;
        this.learningCatalogueService = learningCatalogueService;
        this.learnerRecordService = learnerRecordService;
        this.moduleRecordActionService = moduleRecordActionService;
    }

    public ModuleRecord launchModule(String learnerId, Course course, Module module, ModuleLaunchLinkInput moduleLaunchLinkInput) {
        ModuleRecordUpdate update = moduleRecordActionService.getLaunchModuleUpdate(moduleLaunchLinkInput.getCourseIsRequired());
        if (List.of(ModuleType.link, ModuleType.file).contains(module.getModuleType())) {
            update = moduleRecordActionService.getCompleteModuleUpdate(course, module);
        }
        CourseRecord courseRecord = processModuleRecordAction(learnerId, course.getId(), module.getId(), update);
        return courseRecord.getModuleRecord(module.getId());
    }

    public ModuleResponse completeModule(String learnerId, String courseId, String moduleId) {
        CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(courseId, moduleId);
        ModuleRecordUpdate update = moduleRecordActionService.getCompleteModuleUpdate(courseWithModule.getCourse(), courseWithModule.getModule());
        CourseRecord courseRecord = processModuleRecordAction(learnerId, courseId, moduleId, update);
        return new ModuleResponse("Module was successfully completed", courseRecord.getCourseTitle(),
                courseRecord.getModuleRecord(moduleId).getModuleTitle(), courseId, moduleId);
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
        return learnerRecordService.updateCourseRecordCache(courseRecord);
    }

}
