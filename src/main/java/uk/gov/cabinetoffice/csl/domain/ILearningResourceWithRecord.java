package uk.gov.cabinetoffice.csl.domain;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.ILearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;

public interface ILearningResourceWithRecord extends ILearningResource {
    ILearnerRecord getRecord();

    String getLearnerId();

    default LearnerRecordResourceId getRecordResourceId() {
        return new LearnerRecordResourceId(this.getType(), this.getLearnerId(), this.getResourceId());
    }
}
