package uk.gov.cabinetoffice.csl.service;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionWithId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordData;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEventData;

@Component
public class LearnerRecordActionWorker {

    private final LearnerRecordDataFactory learnerRecordDataFactory;

    public LearnerRecordActionWorker(LearnerRecordDataFactory learnerRecordDataFactory) {
        this.learnerRecordDataFactory = learnerRecordDataFactory;
    }

    private boolean shouldAddEvent(ILearnerRecordActionType latestAction, ILearnerRecordActionType action) {
        return latestAction == null || (action.canRepeat() || !latestAction.equals(action));
    }

    public LearnerRecordData processAction(@Nullable LearnerRecord learnerRecord, ActionWithId actionWithId) {
        LearnerRecordData learnerRecordData = learnerRecord == null ? null : learnerRecordDataFactory.createRecordData(learnerRecord);
        ILearnerRecordActionType action = actionWithId.getAction();
        if (learnerRecordData == null) {
            if (action.canCreateRecord()) {
                learnerRecordData = learnerRecordDataFactory.createNewRecordData(actionWithId);
            }
        } else {
            if (shouldAddEvent(learnerRecordData.getLatestAction(), action)) {
                LearnerRecordEventData eventData = learnerRecordDataFactory.createNewEventData(actionWithId);
                learnerRecordData.getEvents().add(eventData);
            }
        }
        return learnerRecordData;
    }
}
