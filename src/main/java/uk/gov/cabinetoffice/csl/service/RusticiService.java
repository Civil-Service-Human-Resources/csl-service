package uk.gov.cabinetoffice.csl.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.*;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.*;

@Service
public class RusticiService {

    private final RequestEntityWithBasicAuthFactory requestEntityFactory;

    @Value("${rustici.registrationLaunchLinkUrl}")
    private String registrationLaunchLinkUrl;

    @Value("${rustici.registrationWithLaunchLinkUrl}")
    private String registrationWithLaunchLinkUrl;

    @Value("${rustici.username}")
    private String rusticiUsername;

    @Value("${rustici.password}")
    private String rusticiPassword;

    @Value("${rustici.engineTenantName}")
    private String rusticiEngineTenantName;

    @Value("${rustici.redirectOnExitUrl}")
    private String rusticiRedirectOnExitUrl;

    @Value("${rustici.launchLinkExpiry}")
    private int rusticiLaunchLinkExpiry;

    public RusticiService(RequestEntityWithBasicAuthFactory requestEntityFactory) {
        this.requestEntityFactory = requestEntityFactory;
    }

    public ResponseEntity<?> getRegistrationLaunchLink(RegistrationInput registrationInput) {
        RequestEntity<?> postRequestWithBasicAuth = requestEntityFactory.createPostRequestWithBasicAuth(
                String.format(registrationLaunchLinkUrl, registrationInput.getRegistrationId()),
                createLaunchLinkRequest(String.format(rusticiRedirectOnExitUrl, registrationInput.getCourseId(),
                        registrationInput.getModuleId())),
                rusticiUsername, rusticiPassword, addAdditionalHeaderParams("EngineTenantName", rusticiEngineTenantName));
        return invokeService(postRequestWithBasicAuth);
    }

    public ResponseEntity<?> createRegistrationAndLaunchLink(RegistrationInput registrationInput) {
        RequestEntity<?> postRequestWithBasicAuth = requestEntityFactory.createPostRequestWithBasicAuth(
                registrationWithLaunchLinkUrl, createRegistrationRequest(registrationInput),
                rusticiUsername, rusticiPassword, addAdditionalHeaderParams("EngineTenantName", rusticiEngineTenantName));
        return invokeService(postRequestWithBasicAuth);
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
        registrationRequest.setLaunchLinkRequest(createLaunchLinkRequest(String.format(rusticiRedirectOnExitUrl,
                registrationInput.getCourseId(), registrationInput.getModuleId())));
        return registrationRequest;
    }
}
