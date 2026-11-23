package uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview;

import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;

@NoArgsConstructor
@Getter
public class FileModuleOverview extends ModuleOverview {

    private String filename;
    private String extension;
    private Integer sizeInKb;

    public FileModuleOverview(String id, String title, String description, boolean mandatory,
                              boolean associatedLearning, ModuleType type, Integer duration, Integer costInPounds,
                              State state, boolean mustConfirmBooking, String filename, String extension, Integer sizeInKb) {
        super(id, title, description, mandatory, associatedLearning, type, duration, costInPounds, state, mustConfirmBooking);
        this.filename = filename;
        this.extension = extension;
        this.sizeInKb = sizeInKb;
    }
}
