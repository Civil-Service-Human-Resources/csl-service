package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingStatus;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.util.UtilService;

public class CancelBooking extends EventModuleRecordActionProcessor {

    public CancelBooking(UtilService utilService, CourseWithModuleWithEvent courseWithModuleWithEvent, User user) {
        super(utilService, courseWithModuleWithEvent, user, EventModuleRecordAction.CANCEL_BOOKING);
    }

    @Override
    public ModuleRecord applyUpdatesToModuleRecord(ModuleRecord moduleRecord) {
        moduleRecord.setState(State.UNREGISTERED);
        moduleRecord.setBookingStatus(BookingStatus.CANCELLED);
        moduleRecord.setResult(null);
        moduleRecord.setCompletionDate(null);
        return moduleRecord;
    }

    @Override
    public ModuleRecord generateNewModuleRecord() {
        throw new IncorrectStateException("Can't create a new module record when cancelling an event.");
    }
}
