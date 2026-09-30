package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ModuleOverview {
    private String id;
    private String title;
    private String description;
    private boolean mandatory;
    private boolean associatedLearning;
    private ModuleType type;
    private Integer duration;
    private Integer costInPounds;
    private State state;
}
