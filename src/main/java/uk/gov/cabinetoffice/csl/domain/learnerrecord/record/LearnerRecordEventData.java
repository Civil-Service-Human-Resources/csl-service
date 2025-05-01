package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;

@Data
public class LearnerRecordEventData {

    private final ILearnerRecordActionType actionType;
    private final boolean newEvent;

}
