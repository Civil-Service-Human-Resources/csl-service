package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import uk.gov.cabinetoffice.csl.domain.LearningResourceType;

public interface ILearnerRecordActionType {
    String getDescription();

    String getName();

    LearningResourceType getRecordType();

    boolean canCreateRecord();

    boolean canRepeat();
}
