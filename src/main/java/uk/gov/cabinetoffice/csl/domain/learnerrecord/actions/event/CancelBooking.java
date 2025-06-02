package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.IModuleAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingStatus;

public class CancelBooking implements IModuleAction {

    @Override
    public ModuleRecord applyUpdates(ModuleRecord moduleRecord) {
        moduleRecord.setState(State.UNREGISTERED);
        moduleRecord.setBookingStatus(BookingStatus.CANCELLED);
        moduleRecord.setResult(null);
        moduleRecord.setCompletionDate(null);
        return moduleRecord;
    }

    @Override
    public ICourseRecordActionType getAction() {
        return EventModuleRecordAction.CANCEL_BOOKING;
    }

    @Override
    public boolean canCreateRecord() {
        return false;
    }

}
