package uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ModuleOverviewCollection extends ArrayList<ModuleOverview> {

    private boolean hasFaceToFace;

    public void setAssociatedLearning() {
        if (isHasFaceToFace()) {
            stream().filter(ModuleOverview::isAssociatedLearning).forEach(m -> m.setMustConfirmBooking(true));
        }
    }
}
