package uk.gov.cabinetoffice.csl.client.RusticiEngineClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLinkRequest;
import uk.gov.cabinetoffice.csl.domain.rustici.RegistrationRequest;

@Slf4j
@Service
public class RusticiEngineClient implements IRusticiEngineClient {

    @Value("${rustici.registrationLaunchLinkUrl}")
    private String registrationLaunchLinkUrl;

    @Value("${rustici.registrationWithLaunchLinkUrl}")
    private String registrationWithLaunchLinkUrl;

    private final IHttpClient client;

    public RusticiEngineClient(@Qualifier("rusticiHttpClient") IHttpClient client) {
        this.client = client;
    }

    @Override
    public LaunchLink createLaunchLink(String registrationId, LaunchLinkRequest launchLinkRequest) {
        log.info("Creating launch launch link for module UID '{}'", registrationId);
        try {
            String url = String.format(registrationLaunchLinkUrl, registrationId);
            RequestEntity<LaunchLinkRequest> request = RequestEntity.post(url).body(launchLinkRequest);
            return client.executeRequest(request, LaunchLink.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                log.debug("Registration '{}' doesn't exist.", registrationId);
            }
            return null;
        }
    }

    @Override
    public LaunchLink createLaunchLinkWithRegistration(RegistrationRequest registrationRequest) {
        RequestEntity<RegistrationRequest> request = RequestEntity.post(registrationWithLaunchLinkUrl).body(registrationRequest);
        return client.executeRequest(request, LaunchLink.class);
    }
}
