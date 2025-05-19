package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ITypedLearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class LearnerRecordData {

    private final ITypedLearnerRecordResourceID resourceId;
    private final List<LearnerRecordEventData> events = new ArrayList<>();
    private LocalDateTime createdTimestamp;
    private boolean newRecord;

    public LearnerRecordData(ITypedLearnerRecordResourceID resourceId) {
        this.resourceId = resourceId;
    }

    public ILearnerRecordActionType getLatestAction() {
        return !events.isEmpty() ? events.get(events.size() - 1).getActionType() : null;
    }

}
