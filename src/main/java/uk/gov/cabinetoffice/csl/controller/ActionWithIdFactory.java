package uk.gov.cabinetoffice.csl.controller;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionWithId;

@Service
public class ActionWithIdFactory {

    public ActionWithId create(String resourceId, String userId, ILearnerRecordActionType action) {
        LearnerRecordResourceId id = new LearnerRecordResourceId(action.getRecordType(), resourceId, userId);
        return new ActionWithId(id, action);
    }

}
