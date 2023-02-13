package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.domain.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.RegistrationRequest;

@Slf4j
@Service
public class RusticiService {

    private final RequestEntityFactory requestEntityFactory;

    private final String rusticiRegistrationUrl;

    private final String rusticiUsername;

    private final String rusticiPassword;

    private final RestTemplate restTemplate;

    public RusticiService(RequestEntityFactory requestEntityFactory, RestTemplate restTemplate,
                          @Value("${rustici.registrationUrl}") String rusticiRegistrationUrl,
                          @Value("${rustici.username}") String rusticiUsername,
                          @Value("${rustici.password}") String rusticiPassword) {
        this.requestEntityFactory = requestEntityFactory;
        this.restTemplate = restTemplate;
        this.rusticiRegistrationUrl = rusticiRegistrationUrl;
        this.rusticiUsername = rusticiUsername;
        this.rusticiPassword = rusticiPassword;
    }

    public ResponseEntity<?> getRegistrationLaunchLink(String registrationId, RegistrationRequest registrationRequest) {
        String url = rusticiRegistrationUrl + "/" + registrationId + "/launchLink";
        return getLaunchLink(url, registrationRequest);
    }

    public ResponseEntity<?> createRegistrationAndLaunchLink(RegistrationRequest registrationRequest) {
        String url = rusticiRegistrationUrl + "/withLaunchLink";
        return getLaunchLink(url, registrationRequest);
    }

    private ResponseEntity<?> getLaunchLink(String url, RegistrationRequest registrationRequest) {

        RequestEntity<?> postRequestWithBasicAuth = requestEntityFactory.createPostRequestWithBasicAuth(
                url, registrationRequest, rusticiUsername, rusticiPassword);

        ResponseEntity<LaunchLink> response = restTemplate.exchange(postRequestWithBasicAuth, LaunchLink.class);

        if(response.getStatusCode().is2xxSuccessful()) {
            LaunchLink launchLink = response.getBody();
            assert launchLink != null;
            log.debug("launchLink: {}", launchLink.getLaunchLink());
        }
        return response;
    }
}