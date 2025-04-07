package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingStatus;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.time.LocalDateTime;

public class CancelBooking extends EventModuleRecordActionProcessor {

    public CancelBooking(UtilService utilService, CourseWithModuleWithEvent courseWithModuleWithEvent, User user) {
        super(utilService, courseWithModuleWithEvent, user, EventModuleRecordAction.CANCEL_BOOKING);
    }

    @Override
    public CourseRecord updateCourseRecord(CourseRecord courseRecord, LocalDateTime completionDate) {
        ModuleRecord moduleRecord = courseRecord.getModuleRecord(getModuleId())
                .orElseThrow(() -> new IncorrectStateException("Can't create a new module record when cancelling an event."));
        if (courseRecord.getState().equals(State.NULL) ||
                !courseRecord.getState().equals(State.IN_PROGRESS)) {
            courseRecord.setState(State.UNREGISTERED);
        }
        moduleRecord.setState(State.UNREGISTERED);
        moduleRecord.setBookingStatus(BookingStatus.CANCELLED);
        moduleRecord.setResult(null);
        moduleRecord.setCompletionDate(null);
        return courseRecord;
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        throw new IncorrectStateException("Can't create a new course record when cancelling an event.");
    }
}
