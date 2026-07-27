package uk.gov.cabinetoffice.csl.domain.learning;

import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;

import java.util.Collection;

public record LearningTagTaxonomy(LearningTag category, Collection<LearningTag> parents,
                                  Collection<LearningTag> children) {
}
