package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;

public class CompleteBooking extends EventModuleRecordActionProcessor {

    public CompleteBooking(CourseWithModuleWithEvent courseWithModuleWithEvent, User user) {
        super(courseWithModuleWithEvent, user, EventModuleRecordAction.COMPLETE_BOOKING);
    }

    @Override
    public CourseRecord updateCourseRecord(CourseRecord courseRecord) {
        ModuleRecord moduleRecord = courseRecord.getModuleRecord(getModuleId())
                .orElseThrow(() -> new IncorrectStateException("Can't create a new module record when completing an event."));
        if (!moduleRecord.getState().equals(State.APPROVED) ||
                !courseRecord.getState().equals(State.APPROVED)) {
            throw new IncorrectStateException("Can't complete a booking that hasn't been approved");
        }
        if (willModuleCompletionCompleteCourse(courseRecord)) {
            courseRecord.setState(State.COMPLETED);
            messages.add(generateCompletionMessage());
        }
        moduleRecord.setState(State.COMPLETED);
        return courseRecord;
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        throw new IncorrectStateException("Can't create a new course record when completing an event.");
    }
}
