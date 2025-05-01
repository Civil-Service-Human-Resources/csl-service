package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class LearnerRecordData {

    private final LearnerRecordResourceId resourceId;
    private final List<LearnerRecordEventData> events = new ArrayList<>();
    private final boolean newRecord;

    public void addNewEvent(ILearnerRecordActionType learnerRecordAction) {
        LearnerRecordEventData event = new LearnerRecordEventData(learnerRecordAction, true);
        events.add(event);
    }

    public ILearnerRecordActionType getLatestAction() {
        return !events.isEmpty() ? events.get(events.size() - 1).getActionType() : null;
    }

    public LearnerRecordData(LearnerRecordResourceId id, ILearnerRecordActionType learnerRecordAction) {
        this(id, true);
        this.addNewEvent(learnerRecordAction);
    }

    public boolean shouldAddEvent(ILearnerRecordActionType action) {
        return getLatestAction() == null || (action.canRepeat() || !getLatestAction().equals(action));
    }
}
