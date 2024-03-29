package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.RusticiEngineClient.IRusticiEngineClient;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Result;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.ModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.rustici.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Slf4j
public class RusticiService {

    @Value("${rustici.redirectOnExitUrl}")
    private String rusticiRedirectOnExitUrl;

    @Value("${rustici.launchLinkExpirySeconds}")
    private int rusticiLaunchLinkExpiry;

    @Value("${rustici.disabledBookmarkingModuleIDs}")
    private String[] disabledBookmarkingModuleIDs;

    private final IRusticiEngineClient rusticiEngineClient;

    public RusticiService(IRusticiEngineClient rusticiEngineClient) {
        this.rusticiEngineClient = rusticiEngineClient;
    }

    public CSLRusticiProps getCSLDataFromRollUpData(RusticiRollupData rollupData) {
        String rusticiCourseIdRegex = "\\.";
        String[] courseIdDotModuleIdParts = rollupData.getCourse().getId().split(rusticiCourseIdRegex);
        String courseId = courseIdDotModuleIdParts[0];
        String moduleId = courseIdDotModuleIdParts[1];
        String learnerId = rollupData.getLearner().getId();

        List<ModuleRecordAction> moduleRecordActionList = new ArrayList<>();
        if (rollupData.getCompletedDate() != null) {
            moduleRecordActionList.add(ModuleRecordAction.ROLLUP_COMPLETE_MODULE);
        }

        String resultStr = rollupData.getRegistrationSuccess();
        Result result = null;
        if (isNotBlank(resultStr)) {
            for (Result v : Result.values()) {
                if (v.name().equals(resultStr)) {
                    result = v;
                    break;
                }
            }
        }

        if (result != null) {
            if (result.equals(Result.FAILED)) {
                moduleRecordActionList.add(ModuleRecordAction.FAIL_MODULE);
            } else {
                moduleRecordActionList.add(ModuleRecordAction.PASS_MODULE);
            }
        }

        return new CSLRusticiProps(
                courseId,
                moduleId,
                learnerId,
                moduleRecordActionList
        );
    }

    public LaunchLink createLaunchLink(RegistrationInput registrationInput) {
        LaunchLink launchLink = getRegistrationLaunchLink(registrationInput);
        if (launchLink == null) {
            //If no launch link present then create the registration and launch link using withLaunchLink
            launchLink = createRegistrationAndLaunchLink(registrationInput);
        }
        if (Arrays.stream(disabledBookmarkingModuleIDs).anyMatch(registrationInput.getModuleId()::equalsIgnoreCase)) {
            launchLink.clearBookmarking();
            log.info("Module launch link is updated for clearbookmark=true for learner id: "
                    + "{}, course id: {} and module id: {}", registrationInput.getLearnerId(), registrationInput.getCourseId(), registrationInput.getModuleId());
        }
        return launchLink;
    }

    private LaunchLink getRegistrationLaunchLink(RegistrationInput registrationInput) {
        String redirectOnExitUrl = createRedirectOnExitUrl(registrationInput.getCourseId(), registrationInput.getModuleId());
        LaunchLinkRequest requestBody = createLaunchLinkRequest(redirectOnExitUrl);
        return rusticiEngineClient.createLaunchLink(registrationInput.getRegistrationId(), requestBody);
    }

    private LaunchLink createRegistrationAndLaunchLink(RegistrationInput registrationInput) {
        RegistrationRequest requestBody = createRegistrationRequest(registrationInput);
        return rusticiEngineClient.createLaunchLinkWithRegistration(requestBody);
    }

    private LaunchLinkRequest createLaunchLinkRequest(String redirectOnExitUrl) {
        LaunchLinkRequest launchLinkRequest = new LaunchLinkRequest();
        launchLinkRequest.setExpiry(rusticiLaunchLinkExpiry);
        launchLinkRequest.setRedirectOnExitUrl(redirectOnExitUrl);
        return launchLinkRequest;
    }

    private RegistrationRequest createRegistrationRequest(RegistrationInput registrationInput) {
        Learner learner = new Learner();
        learner.setId(registrationInput.getLearnerId());
        learner.setFirstName(registrationInput.getLearnerFirstName());
        learner.setLastName(registrationInput.getLearnerLastName());

        Registration registration = new Registration();
        registration.setRegistrationId(registrationInput.getRegistrationId());
        registration.setCourseId(registrationInput.getCourseId() + "." + registrationInput.getModuleId());
        registration.setLearner(learner);

        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setRegistration(registration);
        String redirectOnExitUrl = createRedirectOnExitUrl(registrationInput.getCourseId(), registrationInput.getModuleId());
        registrationRequest.setLaunchLinkRequest(createLaunchLinkRequest(redirectOnExitUrl));
        return registrationRequest;
    }

    private String createRedirectOnExitUrl(String courseId, String moduleId) {
        return String.format(rusticiRedirectOnExitUrl, courseId, moduleId);
    }
}
