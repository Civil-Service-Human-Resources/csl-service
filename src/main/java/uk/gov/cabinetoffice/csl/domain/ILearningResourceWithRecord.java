package uk.gov.cabinetoffice.csl.domain;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;

public interface ILearningResourceWithRecord extends ILearningResource {
    LearnerRecord getRecord();

    String getLearnerId();

}
