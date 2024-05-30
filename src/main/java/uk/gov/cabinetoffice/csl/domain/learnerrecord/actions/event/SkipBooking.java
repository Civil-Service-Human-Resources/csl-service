package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.util.UtilService;

public class SkipBooking extends EventModuleRecordActionProcessor {

    public SkipBooking(UtilService utilService, CourseWithModuleWithEvent courseWithModuleWithEvent, User user) {
        super(utilService, courseWithModuleWithEvent, user, EventModuleRecordAction.SKIP_BOOKING);
    }

    @Override
    public CourseRecord updateCourseRecord(CourseRecord courseRecord) {
        ModuleRecord moduleRecord = courseRecord.getModuleRecord(getModuleId())
                .orElseThrow(() -> new IncorrectStateException("Can't create a new module record when skipping an event."));
        if (!moduleRecord.getState().equals(State.APPROVED)) {
            throw new IncorrectStateException("Can't complete a booking that hasn't been approved");
        }
        if (courseRecord.getState().equals(State.APPROVED)) {
            courseRecord.setState(State.SKIPPED);
        }
        moduleRecord.setState(State.SKIPPED);
        moduleRecord.setBookingStatus(null);
        moduleRecord.setResult(null);
        moduleRecord.setCompletionDate(null);
        return courseRecord;
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        throw new IncorrectStateException("Can't create a new course record when skipping an event.");
    }
}
