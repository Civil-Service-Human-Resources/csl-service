package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class LearnerRecordEventData {

    private final ILearnerRecordActionType actionType;
    private LocalDateTime timestamp;
    private final boolean newEvent;

    public LearnerRecordEventData(ILearnerRecordActionType actionType, LocalDateTime timestamp, boolean newEvent) {
        this(actionType, newEvent);
        this.timestamp = timestamp;
    }
    
}
