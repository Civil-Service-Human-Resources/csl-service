package uk.gov.cabinetoffice.csl.client.RusticiEngineClient;

import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLinkRequest;
import uk.gov.cabinetoffice.csl.domain.rustici.RegistrationRequest;

public interface IRusticiEngineClient {
    boolean doesRegistrationExist(String registrationId);

    LaunchLink createLaunchLink(String registrationId, LaunchLinkRequest launchLinkRequest);

    LaunchLink createLaunchLinkWithRegistration(RegistrationRequest registrationRequest);
}
