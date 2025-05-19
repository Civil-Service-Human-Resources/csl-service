package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.*;

@Service
public class LearnerRecordDataFactory {

    public LearnerRecordData createRecordData(LearnerRecord learnerRecord) {
        LearnerRecordData data = new LearnerRecordData(learnerRecord.getLearnerRecordId());
        data.setNewRecord(false);
        data.setCreatedTimestamp(learnerRecord.getCreatedTimestamp());
        LearnerRecordEvent latestEvent = learnerRecord.getLatestEvent();
        if (latestEvent != null) {
            LearnerRecordEventData event = new LearnerRecordEventData(latestEvent.getActionType(), latestEvent.getEventTimestamp(), false);
            data.getEvents().add(event);
        }
        return data;
    }

    public LearnerRecordData createNewRecordData(ActionWithId actionWithId) {
        LearnerRecordData data = new LearnerRecordData(actionWithId.getResourceId());
        data.setNewRecord(true);
        if (actionWithId.getTimestamp() != null) {
            data.setCreatedTimestamp(actionWithId.getTimestamp());
        }
        LearnerRecordEventData event = createNewEventData(actionWithId);
        data.getEvents().add(event);
        return data;
    }

    public LearnerRecordEventData createNewEventData(ActionWithId actionWithId) {
        LearnerRecordEventData event = new LearnerRecordEventData(actionWithId.getAction(), true);
        if (actionWithId.getTimestamp() != null) {
            event.setTimestamp(actionWithId.getTimestamp());
        }
        return event;
    }

}
