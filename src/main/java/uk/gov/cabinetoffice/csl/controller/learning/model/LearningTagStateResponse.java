package uk.gov.cabinetoffice.csl.controller.learning.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagStateUpdate;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class LearningTagStateResponse {
    private LearningTagStateUpdate state;
    private Collection<Long> updatedIds;
}
