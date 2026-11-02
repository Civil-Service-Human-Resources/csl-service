package uk.gov.cabinetoffice.csl.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LaunchLinkResponse extends LaunchLink {
    private String moduleTitle;
    private ModuleType moduleType;

    public LaunchLinkResponse(String launchLink, String moduleTitle, ModuleType moduleType) {
        super(launchLink);
        this.moduleTitle = moduleTitle;
        this.moduleType = moduleType;
    }
}
