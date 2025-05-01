package uk.gov.cabinetoffice.csl.service;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ILearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionWithId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordData;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEventData;

@Service
public class LearnerRecordDataFactory {

    public LearnerRecordData createNewRecordData(LearnerRecordResourceId id) {
        return createNewRecordData(id, null);
    }

    public LearnerRecordData createNewRecordData(LearnerRecordResourceId id, @Nullable ILearnerRecordActionType action) {
        LearnerRecordData data = new LearnerRecordData(id, true);
        if (action != null) {
            LearnerRecordEventData event = new LearnerRecordEventData(action, true);
            data.getEvents().add(event);
        }
        return data;
    }

    public LearnerRecordData createRecordData(ILearnerRecord learnerRecord) {
        return createRecordData(learnerRecord.getLearnerRecordId(), learnerRecord.getLatestEvent().getActionType());
    }

    public LearnerRecordData createRecordData(LearnerRecordResourceId id, @Nullable ILearnerRecordActionType existingAction) {
        LearnerRecordData data = new LearnerRecordData(id, false);
        if (existingAction != null) {
            LearnerRecordEventData event = new LearnerRecordEventData(existingAction, false);
            data.getEvents().add(event);
        }
        return data;
    }

    public LearnerRecordData createNewRecordData(ActionWithId actionWithId) {
        return createNewRecordData(actionWithId.getLearnerRecordId(), actionWithId.getAction());
    }
}
