package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ITypedLearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.TypedLearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LearnerRecordEvent implements Serializable {

    private Long learnerRecordId;
    private String resourceId;
    private String learnerId;
    private ILearnerRecordActionType actionType;
    private LearnerRecordEventType eventType;
    private LearnerRecordEventSource eventSource;
    private LocalDateTime eventTimestamp;

    public ITypedLearnerRecordResourceID getResourceId() {
        return new TypedLearnerRecordResourceId(learnerId, resourceId, actionType.getRecordType());
    }
}
