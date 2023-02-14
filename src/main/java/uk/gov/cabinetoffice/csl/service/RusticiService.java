package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.domain.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class RusticiService {

    private final RequestEntityFactory requestEntityFactory;

    private final String rusticiRegistrationUrl;

    private final String rusticiUsername;

    private final String rusticiPassword;

    private final String rusticiEngineTenantName;

    private final String rusticiRedirectOnExitUrl;

    private final RestTemplate restTemplate;

    public RusticiService(RequestEntityFactory requestEntityFactory, RestTemplate restTemplate,
                          @Value("${rustici.registrationUrl}") String rusticiRegistrationUrl,
                          @Value("${rustici.username}") String rusticiUsername,
                          @Value("${rustici.password}") String rusticiPassword,
                          @Value("${rustici.engineTenantName}") String rusticiEngineTenantName,
                          @Value("${rustici.redirectOnExitUrl}") String rusticiRedirectOnExitUrl) {
        this.requestEntityFactory = requestEntityFactory;
        this.restTemplate = restTemplate;
        this.rusticiRegistrationUrl = rusticiRegistrationUrl;
        this.rusticiUsername = rusticiUsername;
        this.rusticiPassword = rusticiPassword;
        this.rusticiEngineTenantName = rusticiEngineTenantName;
        this.rusticiRedirectOnExitUrl = rusticiRedirectOnExitUrl;
    }

    public ResponseEntity<?> getRegistrationLaunchLink(String registrationId, String courseId, String moduleId) {

        RequestEntity<?> postRequestWithBasicAuth = requestEntityFactory.createPostRequestWithBasicAuth(
                rusticiRegistrationUrl + "/" + registrationId + "/launchLink",
                createLaunchLinkRequest(rusticiRedirectOnExitUrl + "/" + courseId + "/" + moduleId),
                rusticiUsername, rusticiPassword, addAdditionalHeaderParams());

        return getLaunchLink(postRequestWithBasicAuth);
    }

    public ResponseEntity<?> createRegistrationAndLaunchLink(String registrationId, String courseId, String moduleId,
                                                             String learnerFirstName, String learnerId) {
        RequestEntity<?> postRequestWithBasicAuth = requestEntityFactory.createPostRequestWithBasicAuth(
                rusticiRegistrationUrl + "/withLaunchLink",
                createRegistrationRequest(registrationId, courseId, moduleId, learnerFirstName, learnerId),
                rusticiUsername, rusticiPassword, addAdditionalHeaderParams());

        return getLaunchLink(postRequestWithBasicAuth);
    }

    private LaunchLinkRequest createLaunchLinkRequest(String redirectOnExitUrl) {

        LaunchLinkRequest launchLinkRequest = new LaunchLinkRequest();
        launchLinkRequest.setExpiry(0);
        launchLinkRequest.setRedirectOnExitUrl(redirectOnExitUrl);

        return launchLinkRequest;
    }

    private RegistrationRequest createRegistrationRequest(String registrationId, String courseId, String moduleId,
                                                          String learnerFirstName, String learnerId) {
        Learner learner = new Learner();
        learner.setId(learnerId);
        learner.setFirstName(learnerFirstName);

        Registration registration = new Registration();
        registration.setRegistrationId(registrationId);
        registration.setCourseId(courseId);
        registration.setLearner(learner);

        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setRegistration(registration);
        registrationRequest.setLaunchLinkRequest(createLaunchLinkRequest(
                rusticiRedirectOnExitUrl + "/" + courseId + "/" + moduleId));

        return registrationRequest;
    }

    private ResponseEntity<?> getLaunchLink(RequestEntity<?> postRequestWithBasicAuth) {

        ResponseEntity<?> response = restTemplate.exchange(postRequestWithBasicAuth, LaunchLink.class);
        if(response.getStatusCode().is2xxSuccessful()) {
            LaunchLink launchLink = (LaunchLink)response.getBody();
            assert launchLink != null;
            log.debug("launchLink: {}", launchLink.getLaunchLink());
        }

        return response;
    }

    private Map<String, String> addAdditionalHeaderParams() {
        Map<String, String> additionalHeaderParams = new HashMap<>();
        additionalHeaderParams.put("EngineTenantName", rusticiEngineTenantName);
        return additionalHeaderParams;
    }
}
