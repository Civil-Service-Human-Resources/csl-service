package uk.gov.cabinetoffice.csl.controller;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ITypedLearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.TypedLearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionWithId;

@Service
public class ActionWithIdFactory {

    public ActionWithId create(String resourceId, String userId, ILearnerRecordActionType action) {
        ITypedLearnerRecordResourceID id = new TypedLearnerRecordResourceId(userId, resourceId, action.getRecordType());
        return new ActionWithId(id, action);
    }

}
