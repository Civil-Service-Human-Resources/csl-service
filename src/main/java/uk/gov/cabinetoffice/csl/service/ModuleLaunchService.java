package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.ModuleLaunchLinkInput;
import uk.gov.cabinetoffice.csl.domain.rustici.RegistrationInput;

import java.util.Arrays;

@Slf4j
@Service
public class ModuleLaunchService {

    private final ModuleService moduleService;
    private final RusticiService rusticiService;
    private final String[] disabledBookmarkingModuleIDs;

    public ModuleLaunchService(ModuleService moduleService, RusticiService rusticiService,
                               @Value("${rustici.disabledBookmarkingModuleIDs}") String[] disabledBookmarkingModuleIDs) {
        this.moduleService = moduleService;
        this.rusticiService = rusticiService;
        this.disabledBookmarkingModuleIDs = disabledBookmarkingModuleIDs;
    }

    public LaunchLink createLaunchLink(String learnerId, String courseId,
                                       String moduleId, ModuleLaunchLinkInput moduleLaunchLinkInput) {
        log.info("User '{}' launching module '{}' in course '{}'", learnerId, moduleId, courseId);
        log.debug("moduleLaunchLinkInput: {}", moduleLaunchLinkInput);
        ModuleRecord moduleRecord = moduleService.launchModule(learnerId, courseId, moduleId, moduleLaunchLinkInput);
        String moduleRecordUid = moduleRecord.getUid();
        RegistrationInput registrationInput = RegistrationInput.from(learnerId, moduleId, moduleRecordUid,
                courseId, moduleLaunchLinkInput);
        LaunchLink launchLink = rusticiService.createLaunchLink(registrationInput);
        if (isDisabledBookmarkingModuleID(registrationInput.getModuleId())) {
            launchLink.clearBookmarking();
            log.info("Module launch link is updated for clearbookmark=true for learner id: "
                    + "{}, course id: {} and module id: {}", registrationInput.getLearnerId(), registrationInput.getCourseId(), registrationInput.getModuleId());
        }
        return launchLink;
    }

    private boolean isDisabledBookmarkingModuleID(String moduleId) {
        return Arrays.stream(disabledBookmarkingModuleIDs).anyMatch(moduleId::equalsIgnoreCase);
    }
}
