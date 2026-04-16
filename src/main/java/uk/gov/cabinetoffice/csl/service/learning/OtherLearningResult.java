package uk.gov.cabinetoffice.csl.service.learning;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.util.Collection;

public record OtherLearningResult(Collection<LearnerRecord> records,
                                  Collection<Course> courses,
                                  Collection<String> courseIds,
                                  Integer totalElements) {
}
