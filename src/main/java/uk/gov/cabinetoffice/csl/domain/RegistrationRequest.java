package uk.gov.cabinetoffice.csl.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    private String courseId;
    private Learner learner;
    private String registrationId;
    @JsonProperty("launchLink")
    private LaunchLinkRequest launchLinkRequest;
}
