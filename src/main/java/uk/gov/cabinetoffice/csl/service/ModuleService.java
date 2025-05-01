package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.ModuleResponse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordUpdateProcessor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.ModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.rustici.*;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModuleService {

    private final LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;
    private final LearningCatalogueService learningCatalogueService;
    private final RusticiService rusticiService;
    private final UserDetailsService userDetailsService;

    public LaunchLink launchModule(User user, String courseId, String moduleId, UserDetailsDto userDetailsDto) {
        CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(courseId, moduleId);
        Module module = courseWithModule.getModule();
        if (module.isType(ModuleType.link) || module.isType(ModuleType.file)) {
            learnerRecordUpdateProcessor.processModuleRecordAction(courseWithModule, user, ModuleRecordAction.COMPLETE_MODULE);
        } else {
            ModuleRecord moduleRecord = learnerRecordUpdateProcessor.processModuleRecordAction(courseWithModule, user, ModuleRecordAction.LAUNCH_MODULE);
            if (module.isType(ModuleType.elearning)) {
                return rusticiService.createLaunchLink(RegistrationInput.from(
                        user.getId(), moduleId, moduleRecord.getUid(), courseId, userDetailsDto
                ));
            }
        }
        return new LaunchLink(courseWithModule.getModule().getUrl());
    }

    public ModuleResponse completeModule(User user, String courseId, String moduleId, LocalDateTime completionDate) {
        CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(courseId, moduleId);
        learnerRecordUpdateProcessor.processModuleRecordAction(courseWithModule, user, ModuleRecordAction.COMPLETE_MODULE, completionDate);
        return ModuleResponse.fromMetada(ModuleRecordAction.COMPLETE_MODULE, courseWithModule);
    }

    public void processRusticiRollupData(RusticiRollupData rusticiRollupData) {
        log.info("rusticiRollupData: {}", rusticiRollupData);
        CSLRusticiProps properties = rusticiService.getCSLDataFromRollUpData(rusticiRollupData);
        if (!properties.getModuleRecordActions().isEmpty()) {
            CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(properties.getCourseId(), properties.getModuleId());
            User user = userDetailsService.getUserWithUid(properties.getLearnerId());
            learnerRecordUpdateProcessor.processModuleRecordActions(courseWithModule, user, properties.getModuleRecordActions(), rusticiRollupData.getCompletedDate());
        }
    }

}
