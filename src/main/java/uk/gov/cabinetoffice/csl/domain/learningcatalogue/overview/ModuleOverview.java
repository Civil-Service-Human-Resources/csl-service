package uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    private boolean mustConfirmBooking;
}
