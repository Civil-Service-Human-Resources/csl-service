package uk.gov.cabinetoffice.csl.domain.learning;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;

@Data
@RequiredArgsConstructor
public class DisplayAudience {

    private final String name;
    private final String frequency;
    private final LearningPeriod learningPeriod;

}
