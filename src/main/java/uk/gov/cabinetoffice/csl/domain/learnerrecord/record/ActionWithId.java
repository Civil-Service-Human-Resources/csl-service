package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;

@Data
public class ActionWithId {
    private final LearnerRecordResourceId resourceId;
    private final ILearnerRecordActionType action;
}
