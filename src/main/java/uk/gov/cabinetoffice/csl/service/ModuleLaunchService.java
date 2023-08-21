package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.error.GenericServerException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.ModuleLaunchLinkInput;
import uk.gov.cabinetoffice.csl.domain.rustici.RegistrationInput;
import uk.gov.cabinetoffice.csl.util.StringUtilService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ModuleLaunchService {

    private final LearnerRecordService learnerRecordService;
    private final RusticiService rusticiService;
    private final StringUtilService stringUtilService;
    private final Clock clock;
    private final String[] disabledBookmarkingModuleIDs;

    public ModuleLaunchService(LearnerRecordService learnerRecordService, RusticiService rusticiService,
                               StringUtilService stringUtilService, Clock clock,
                               @Value("${rustici.disabledBookmarkingModuleIDs}") String[] disabledBookmarkingModuleIDs) {
        this.learnerRecordService = learnerRecordService;
        this.rusticiService = rusticiService;
        this.stringUtilService = stringUtilService;
        this.clock = clock;
        this.disabledBookmarkingModuleIDs = disabledBookmarkingModuleIDs;
    }

    public LaunchLink createLaunchLink(String learnerId, String courseId,
                                       String moduleId, ModuleLaunchLinkInput moduleLaunchLinkInput) {
        log.info("User '{}' launching module '{}' in course '{}'", learnerId, moduleId, courseId);
        log.debug("moduleLaunchLinkInput: {}", moduleLaunchLinkInput);
        LaunchLink launchLink = null;
        String moduleRecordUid = stringUtilService.generateRandomUuid();
        try {
            CourseRecord courseRecord = learnerRecordService.getCourseRecord(learnerId, courseId);
            if (courseRecord == null) {
                //If the course record is not present then create the course record along with module record
                courseRecord = learnerRecordService.createCourseRecord(learnerId, courseId, moduleId,
                        CourseRecordStatus.builder().state(State.IN_PROGRESS.name())
                                .isRequired(moduleLaunchLinkInput.getCourseIsRequired()).build(),
                        ModuleRecordStatus.builder().uid(moduleRecordUid).state(State.IN_PROGRESS.name()).build());
            }
            if (courseRecord.getState() == null || courseRecord.getState().equals(State.ARCHIVED)) {
                //Update the course record status if it is null or ARCHIVED
                courseRecord = learnerRecordService.updateCourseRecord(learnerId, courseId,
                        List.of(PatchOp.replacePatch("state", State.IN_PROGRESS.name())));
            }
            //Retrieve the relevant module record from the course record
            ModuleRecord moduleRecord = courseRecord.getModuleRecord(moduleId);
            if (moduleRecord == null) {
                //If the relevant module record is not present then create the module record
                moduleRecord = learnerRecordService.createModuleRecord(learnerId, courseId, moduleId,
                        ModuleRecordStatus.builder().uid(moduleRecordUid).state(State.IN_PROGRESS.name()).build());
            }
            List<PatchOp> patches = new ArrayList<>();
            patches.add(PatchOp.replacePatch("/updatedAt", LocalDateTime.now(clock).toString()));
            if (StringUtils.isBlank(moduleRecord.getUid())) {
                patches.add(PatchOp.replacePatch("uid", moduleRecordUid));
            } else {
                moduleRecordUid = moduleRecord.getUid();
            }
            learnerRecordService.updateModuleRecord(moduleRecord.getId(), patches);
            if (StringUtils.isNotBlank(moduleRecordUid)) {
                launchLink = createLaunchLink(learnerId, moduleId, moduleRecordUid, courseId, moduleLaunchLinkInput);
            }
        } catch (Exception e) {
            throw new GenericServerException(String.format("Unable to retrieve launch link for learner id: %s course id: %s and module id: %s. " +
                    "Error: %s", learnerId, courseId, moduleId, e));
        }

        return launchLink;
    }

    private LaunchLink createLaunchLink(String learnerId, String moduleId, String moduleRecordUid,
                                        String courseId, ModuleLaunchLinkInput moduleLaunchLinkInput) {
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
