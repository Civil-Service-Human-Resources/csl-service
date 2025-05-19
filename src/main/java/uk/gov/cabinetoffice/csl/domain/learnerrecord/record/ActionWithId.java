package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ITypedLearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ActionWithId {
    private final ITypedLearnerRecordResourceID resourceId;
    private final ILearnerRecordActionType action;
    private LocalDateTime timestamp;

    public ActionWithId(ITypedLearnerRecordResourceID resourceId, LocalDateTime timestamp, ILearnerRecordActionType action) {
        this(resourceId, action);
        this.timestamp = timestamp;
    }
}
