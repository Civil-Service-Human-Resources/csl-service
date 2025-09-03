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
import uk.gov.cabinetoffice.csl.domain.rustici.*;
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

    public LaunchLink launchModule(User user, String courseId, String moduleId, UserDetailsDto userDetailsDto) {
        CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(courseId, moduleId);
        Module module = courseWithModule.getModule();
        if (module.isType(ModuleType.link) || module.isType(ModuleType.file)) {
            moduleActionService.completeModule(courseWithModule, user);
        } else {
            ModuleRecord moduleRecord = moduleActionService.launchModule(courseWithModule, user);
            if (module.isType(ModuleType.elearning)) {
                return rusticiService.createLaunchLink(RegistrationInput.from(
                        user.getId(), moduleId, moduleRecord.getUid(), courseId, userDetailsDto
                ));
            }
        }
        return new LaunchLink(courseWithModule.getModule().getUrl());
    }

    public ModuleResponse completeModule(User user, String courseId, String moduleId) {
        CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(courseId, moduleId);
        moduleActionService.completeModule(courseWithModule, user);
        return ModuleResponse.fromMetada(ModuleRecordAction.COMPLETE_MODULE, courseWithModule);
    }

    public void processRusticiRollupData(RusticiRollupData rusticiRollupData) {
        log.info("rusticiRollupData: {}", rusticiRollupData);
        CSLRusticiProps properties = rusticiService.getCSLDataFromRollUpData(rusticiRollupData);
        if (properties.shouldProcess()) {
            User user = userDetailsService.getUserWithUid(properties.getLearnerId());
            CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(properties.getCourseId(), properties.getModuleId());
            moduleActionService.rollUpModule(courseWithModule, user, properties);
            frontendClient.clearLearningCaches(user.getId(), properties.getCourseId());
        }
    }

}
