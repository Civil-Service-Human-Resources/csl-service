package uk.gov.cabinetoffice.csl.domain.learning;

import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;

import java.util.Collection;
import java.util.LinkedList;

public record LearningTagTaxonomy(LearningTag category, LinkedList<LearningTag> parents,
                                  Collection<LearningTag> children) {
}
