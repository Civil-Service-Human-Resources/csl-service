package uk.gov.cabinetoffice.csl.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LaunchLinkResponse extends LaunchLink {
    private String moduleTitle;

    public LaunchLinkResponse(String launchLink, String moduleTitle) {
        super(launchLink);
        this.moduleTitle = moduleTitle;
    }
}
