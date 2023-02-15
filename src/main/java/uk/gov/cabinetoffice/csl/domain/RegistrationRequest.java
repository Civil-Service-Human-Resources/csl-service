package uk.gov.cabinetoffice.csl.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    private Registration registration;
    @JsonProperty("launchLink")
    private LaunchLinkRequest launchLinkRequest;
}