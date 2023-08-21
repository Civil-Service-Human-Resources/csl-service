package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.RusticiEngineClient.IRusticiEngineClient;
import uk.gov.cabinetoffice.csl.domain.rustici.*;

@Service
@Slf4j
public class RusticiService {

    @Value("${rustici.redirectOnExitUrl}")
    private String rusticiRedirectOnExitUrl;

    @Value("${rustici.launchLinkExpirySeconds}")
    private int rusticiLaunchLinkExpiry;

    private final IRusticiEngineClient rusticiEngineClient;

    public RusticiService(IRusticiEngineClient rusticiEngineClient) {
        this.rusticiEngineClient = rusticiEngineClient;
    }

    public LaunchLink createLaunchLink(RegistrationInput registrationInput) {
        LaunchLink launchLink = getRegistrationLaunchLink(registrationInput);
        if (launchLink == null) {
            //If no launch link present then create the registration and launch link using withLaunchLink
            launchLink = createRegistrationAndLaunchLink(registrationInput);
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
