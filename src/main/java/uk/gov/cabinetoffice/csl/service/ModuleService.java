package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.frontend.IFrontendClient;
import uk.gov.cabinetoffice.csl.controller.model.ModuleResponse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.ModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.rustici.CSLRusticiProps;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.RegistrationInput;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleActionService moduleActionService;
    private final IFrontendClient frontendClient;
    private final LearningCatalogueService learningCatalogueService;
    private final RusticiService rusticiService;
    private final UserDetailsService userDetailsService;

    public LaunchLink launchModule(String userId, String courseId, String moduleId) {
        CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(courseId, moduleId);
        Module module = courseWithModule.getModule();
        if (module.isType(ModuleType.link) || module.isType(ModuleType.file)) {
            moduleActionService.completeModule(courseWithModule, userId);
        } else {
            ModuleRecord moduleRecord = moduleActionService.launchModule(courseWithModule, userId);
            if (module.isType(ModuleType.elearning)) {
                User user = userDetailsService.getUserWithUid(userId);
                return rusticiService.createLaunchLink(RegistrationInput.from(
                        user, moduleId, moduleRecord.getUid(), courseId
                ));
            }
        }
        return new LaunchLink(courseWithModule.getModule().getUrl());
    }

    public ModuleResponse completeModule(String userId, String courseId, String moduleId) {
        CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(courseId, moduleId);
        moduleActionService.completeModule(courseWithModule, userId);
        return ModuleResponse.fromMetada(ModuleRecordAction.COMPLETE_MODULE, courseWithModule);
    }

    public void processRusticiRollupData(RusticiRollupData rusticiRollupData) {
        log.info("rusticiRollupData: {}", rusticiRollupData);
        CSLRusticiProps properties = rusticiService.getCSLDataFromRollUpData(rusticiRollupData);
        if (properties.shouldProcess()) {
            CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(properties.getCourseId(), properties.getModuleId());
            moduleActionService.rollUpModule(courseWithModule, properties);
            frontendClient.clearLearningCaches(properties.getLearnerId(), properties.getCourseId());
        }
    }

}
