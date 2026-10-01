package uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;

@NoArgsConstructor
@Getter
@Setter
public class FaceToFaceModuleOverview extends ModuleOverview {

    private String eventId;
    private boolean canBeBooked;
    private boolean canBeCancelled;

    public FaceToFaceModuleOverview(String id, String title, String description, boolean mandatory,
                                    boolean associatedLearning, ModuleType type, Integer duration, Integer costInPounds,
                                    State state, boolean mustConfirmBooking, String eventId, boolean canBeBooked,
                                    boolean canBeCancelled) {
        super(id, title, description, mandatory, associatedLearning, type, duration, costInPounds, state, mustConfirmBooking);
        this.eventId = eventId;
        this.canBeBooked = canBeBooked;
        this.canBeCancelled = canBeCancelled;
    }
}
