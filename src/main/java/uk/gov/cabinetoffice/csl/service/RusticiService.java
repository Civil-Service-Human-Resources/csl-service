package uk.gov.cabinetoffice.csl.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.domain.*;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.addAdditionalHeaderParams;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.returnError;

@Service
public class RusticiService {

    private final RequestEntityFactory requestEntityFactory;

    private final String registrationLaunchLinkUrl;

    private final String registrationWithLaunchLinkUrl;

    private final String rusticiUsername;

    private final String rusticiPassword;

    private final String rusticiEngineTenantName;

    private final String rusticiRedirectOnExitUrl;

    private final int rusticiLaunchLinkExpiry;

    private final RestTemplate restTemplate;

    public RusticiService(RequestEntityFactory requestEntityFactory, RestTemplate restTemplate,
                          @Value("${rustici.registrationLaunchLinkUrl}") String registrationLaunchLinkUrl,
                          @Value("${rustici.registrationWithLaunchLinkUrl}") String registrationWithLaunchLinkUrl,
                          @Value("${rustici.username}") String rusticiUsername,
                          @Value("${rustici.password}") String rusticiPassword,
                          @Value("${rustici.engineTenantName}") String rusticiEngineTenantName,
                          @Value("${rustici.redirectOnExitUrl}") String rusticiRedirectOnExitUrl,
                          @Value("${rustici.launchLinkExpiry}") int rusticiLaunchLinkExpiry) {
        this.requestEntityFactory = requestEntityFactory;
        this.restTemplate = restTemplate;
        this.registrationLaunchLinkUrl = registrationLaunchLinkUrl;
        this.registrationWithLaunchLinkUrl = registrationWithLaunchLinkUrl;
        this.rusticiUsername = rusticiUsername;
        this.rusticiPassword = rusticiPassword;
        this.rusticiEngineTenantName = rusticiEngineTenantName;
        this.rusticiRedirectOnExitUrl = rusticiRedirectOnExitUrl;
        this.rusticiLaunchLinkExpiry = rusticiLaunchLinkExpiry;
    }

    public ResponseEntity<?> getRegistrationLaunchLink(RegistrationInput registrationInput) {
        RequestEntity<?> postRequestWithBasicAuth = requestEntityFactory.createPostRequestWithBasicAuth(
                String.format(registrationLaunchLinkUrl, registrationInput.getRegistrationId()),
                createLaunchLinkRequest(String.format(rusticiRedirectOnExitUrl, registrationInput.getCourseId(),
                        registrationInput.getModuleId())),
                rusticiUsername, rusticiPassword, addAdditionalHeaderParams("EngineTenantName", rusticiEngineTenantName));

        return getLaunchLink(postRequestWithBasicAuth);
    }

    public ResponseEntity<?> createRegistrationAndLaunchLink(RegistrationInput registrationInput) {
        RequestEntity<?> postRequestWithBasicAuth = requestEntityFactory.createPostRequestWithBasicAuth(
                registrationWithLaunchLinkUrl, createRegistrationRequest(registrationInput),
                rusticiUsername, rusticiPassword, addAdditionalHeaderParams("EngineTenantName", rusticiEngineTenantName));

        return getLaunchLink(postRequestWithBasicAuth);
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

    private ResponseEntity<?> getLaunchLink(RequestEntity<?> postRequestWithBasicAuth) {
        ResponseEntity<?> response = null;
        try {
            response = restTemplate.exchange(postRequestWithBasicAuth, LaunchLink.class);
        } catch (HttpStatusCodeException ex) {
            response = returnError(ex, postRequestWithBasicAuth.getUrl().getPath());
        }
        return response;
    }
}
