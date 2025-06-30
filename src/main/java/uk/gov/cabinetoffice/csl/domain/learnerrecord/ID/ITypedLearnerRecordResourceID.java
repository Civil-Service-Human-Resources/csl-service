package uk.gov.cabinetoffice.csl.domain.learnerrecord.ID;

import uk.gov.cabinetoffice.csl.domain.LearningResourceType;

public interface ITypedLearnerRecordResourceID extends ILearnerRecordResourceID {

    LearningResourceType getType();

}
