package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;

import java.time.LocalDateTime;

public interface ILearnerRecordEvent {
    ILearnerRecordActionType getEventType();

    LearnerRecordEventSource getEventSource();

    LocalDateTime getEventTimestamp();
}
