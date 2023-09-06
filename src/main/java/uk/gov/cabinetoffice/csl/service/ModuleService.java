package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.ModuleResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.IModuleRecordUpdate;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordUpdateProcessor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ModuleRecordUpdateService;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.ModuleLaunchLinkInput;
import uk.gov.cabinetoffice.csl.domain.rustici.RegistrationInput;

@Service
@Slf4j
public class ModuleService {

    private final LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;
    private final LearningCatalogueService learningCatalogueService;
    private final RusticiService rusticiService;
    private final ModuleRecordUpdateService moduleRecordUpdateService;

    public ModuleService(LearnerRecordUpdateProcessor learnerRecordUpdateProcessor, LearningCatalogueService learningCatalogueService,
                         RusticiService rusticiService, ModuleRecordUpdateService moduleRecordUpdateService) {
        this.learnerRecordUpdateProcessor = learnerRecordUpdateProcessor;
        this.learningCatalogueService = learningCatalogueService;
        this.rusticiService = rusticiService;
        this.moduleRecordUpdateService = moduleRecordUpdateService;
    }

    public LaunchLink launchModule(String learnerId, String courseId, String moduleId, ModuleLaunchLinkInput moduleLaunchLinkInput) {
        CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(courseId, moduleId);
        Course course = courseWithModule.getCourse();
        Module module = courseWithModule.getModule();
        IModuleRecordUpdate update = moduleRecordUpdateService.getLaunchModuleUpdate(course, module, moduleLaunchLinkInput.getCourseIsRequired());
        CourseRecord courseRecord = learnerRecordUpdateProcessor.processModuleRecordAction(learnerId, course.getId(), module.getId(), update);
        ModuleRecord moduleRecord = courseRecord.getModuleRecord(moduleId);
        log.info(String.format("Launching %s module '%s' for user '%s'", module.getModuleType(), moduleId, learnerId));
        if (module.getModuleType().equals(ModuleType.elearning)) {
            return rusticiService.createLaunchLink(RegistrationInput.from(
                    learnerId, moduleId, moduleRecord.getUid(), courseId, moduleLaunchLinkInput
            ));
        } else {
            return new LaunchLink(courseWithModule.getModule().getUrl());
        }
    }

    public ModuleResponse completeModule(String learnerId, String courseId, String moduleId) {
        CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(courseId, moduleId);
        IModuleRecordUpdate update = moduleRecordUpdateService.getCompleteModuleUpdate(courseWithModule.getCourse(), courseWithModule.getModule());
        CourseRecord courseRecord = learnerRecordUpdateProcessor.processModuleRecordAction(learnerId, courseId, moduleId, update);
        return new ModuleResponse("Module was successfully completed", courseRecord.getCourseTitle(),
                courseRecord.getModuleRecord(moduleId).getModuleTitle(), courseId, moduleId);
    }

}
