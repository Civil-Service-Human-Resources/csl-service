package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.IModuleAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;

public class SkipBooking implements IModuleAction {

    @Override
    public ModuleRecord applyUpdates(ModuleRecord moduleRecord) {
        if (!moduleRecord.getState().equals(State.APPROVED)) {
            throw new IncorrectStateException("Can't skip a booking that hasn't been approved");
        }
        moduleRecord.setState(State.SKIPPED);
        moduleRecord.setBookingStatus(null);
        moduleRecord.setResult(null);
        moduleRecord.setCompletionDate(null);
        return moduleRecord;
    }

    @Override
    public ICourseRecordActionType getAction() {
        return EventModuleRecordAction.SKIP_BOOKING;
    }

    @Override
    public boolean canCreateRecord() {
        return false;
    }

}
