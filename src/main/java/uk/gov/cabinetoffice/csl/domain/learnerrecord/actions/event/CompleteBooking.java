package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.time.LocalDateTime;

@Slf4j
public class CompleteBooking extends EventModuleRecordActionProcessor {

    public CompleteBooking(UtilService utilService, CourseWithModuleWithEvent courseWithModuleWithEvent, User user) {
        super(utilService, courseWithModuleWithEvent, user, EventModuleRecordAction.COMPLETE_BOOKING);
    }

    @Override
    public CourseRecord updateCourseRecord(CourseRecord courseRecord) {
        ModuleRecord moduleRecord = courseRecord.getModuleRecord(getModuleId())
                .orElseThrow(() -> new IncorrectStateException("Can't create a new module record when completing an event."));
        if (!moduleRecord.getState().equals(State.APPROVED) ||
                !courseRecord.getState().equals(State.APPROVED)) {
            throw new IncorrectStateException("Can't complete a booking that hasn't been approved");
        }
        LocalDateTime completionDate = utilService.getNowDateTime();
        if (willModuleCompletionCompleteCourse(courseRecord)) {
            log.debug(String.format("Completing module %s will complete this course. Setting course record to completed and sending completion message", getModuleId()));
            courseRecord.setState(State.COMPLETED);
            messages.add(generateCompletionMessage(completionDate));
        }
        moduleRecord.setState(State.COMPLETED);
        moduleRecord.setCompletionDate(completionDate);
        return courseRecord;
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        throw new IncorrectStateException("Can't create a new course record when completing an event.");
    }
}
