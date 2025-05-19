package uk.gov.cabinetoffice.csl.domain;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ITypedLearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.TypedLearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;

public interface ILearningResourceWithRecord extends ILearningResource {
    LearnerRecord getRecord();

    String getLearnerId();

    default ITypedLearnerRecordResourceID getRecordResourceId() {
        return new TypedLearnerRecordResourceId(this.getLearnerId(), this.getResourceId(), this.getType());
    }
}
