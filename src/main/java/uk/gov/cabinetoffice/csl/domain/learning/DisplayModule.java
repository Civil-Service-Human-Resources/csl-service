package uk.gov.cabinetoffice.csl.domain.learning;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class DisplayModule {

    private final String id;
    private final String moduleTitle;
    private final String description;
    private final String type;
    private final boolean required;
    private final LocalDateTime lastUpdated;
    private final LocalDateTime completionDate;
    private final State status;

}
