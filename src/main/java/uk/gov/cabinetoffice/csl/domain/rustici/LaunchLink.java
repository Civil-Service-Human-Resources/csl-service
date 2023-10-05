package uk.gov.cabinetoffice.csl.domain.rustici;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LaunchLink {
    private String launchLink;

    public void clearBookmarking() {
        setLaunchLink(String.format("%s&clearbookmark=true", this.launchLink));
    }
}
