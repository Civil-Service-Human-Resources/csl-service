package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
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
    public ModuleRecord applyUpdatesToModuleRecord(ModuleRecord moduleRecord) {
        if (!moduleRecord.getState().equals(State.APPROVED)) {
            throw new IncorrectStateException("Can't complete a booking that hasn't been approved");
        }
        LocalDateTime completionDate = utilService.getNowDateTime();
        moduleRecord.setState(State.COMPLETED);
        moduleRecord.setCompletionDate(completionDate);
        return moduleRecord;
    }

    @Override
    public ModuleRecord generateNewModuleRecord() {
        throw new IncorrectStateException("Can't create a new module record when completing an event.");
    }
}
