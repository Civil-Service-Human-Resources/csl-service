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
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.ModuleLaunchLinkInput;
import uk.gov.cabinetoffice.csl.domain.rustici.RegistrationInput;

@Service
@Slf4j
public class ModuleService {

    private final LearnerRecordActionProcessor learnerRecordActionProcessor;
    private final LearningCatalogueService learningCatalogueService;
    private final RusticiService rusticiService;
    private final ModuleRecordActionService moduleRecordActionService;

    public ModuleService(LearnerRecordActionProcessor learnerRecordActionProcessor, LearningCatalogueService learningCatalogueService,
                         RusticiService rusticiService, ModuleRecordActionService moduleRecordActionService) {
        this.learnerRecordActionProcessor = learnerRecordActionProcessor;
        this.learningCatalogueService = learningCatalogueService;
        this.rusticiService = rusticiService;
        this.moduleRecordActionService = moduleRecordActionService;
    }

    public LaunchLink launchModule(String learnerId, String courseId, String moduleId, ModuleLaunchLinkInput moduleLaunchLinkInput) {
        CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(courseId, moduleId);
        Course course = courseWithModule.getCourse();
        Module module = courseWithModule.getModule();
        ModuleRecordUpdate update = moduleRecordActionService.getLaunchModuleUpdate(course, module, moduleLaunchLinkInput.getCourseIsRequired());
        CourseRecord courseRecord = learnerRecordActionProcessor.processModuleRecordAction(learnerId, course.getId(), module.getId(), update);
        ModuleRecord moduleRecord = courseRecord.getModuleRecord(moduleId);
        return switch (module.getModuleType()) {
            case elearning -> rusticiService.createLaunchLink(RegistrationInput.from(
                    learnerId, moduleId, moduleRecord.getUid(), courseId, moduleLaunchLinkInput
            ));
            case file, link, video -> new LaunchLink(courseWithModule.getModule().getUrl());
        };
    }

    public ModuleResponse completeModule(String learnerId, String courseId, String moduleId) {
        CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(courseId, moduleId);
        ModuleRecordUpdate update = moduleRecordActionService.getCompleteModuleUpdate(courseWithModule.getCourse(), courseWithModule.getModule());
        CourseRecord courseRecord = learnerRecordActionProcessor.processModuleRecordAction(learnerId, courseId, moduleId, update);
        return new ModuleResponse("Module was successfully completed", courseRecord.getCourseTitle(),
                courseRecord.getModuleRecord(moduleId).getModuleTitle(), courseId, moduleId);
    }

}
