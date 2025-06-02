package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.CompleteModule;

import java.time.LocalDateTime;

@Slf4j
public class CompleteBooking extends CompleteModule {

    public CompleteBooking(LocalDateTime completionDate) {
        super(completionDate);
    }

    @Override
    public ModuleRecord applyUpdates(ModuleRecord moduleRecord) {
        if (!moduleRecord.getState().equals(State.APPROVED)) {
            throw new IncorrectStateException("Can't complete a booking that hasn't been approved");
        }
        moduleRecord = super.applyUpdates(moduleRecord);
        return moduleRecord;
    }

    @Override
    public ICourseRecordActionType getAction() {
        return EventModuleRecordAction.COMPLETE_BOOKING;
    }

    @Override
    public boolean canCreateRecord() {
        return false;
    }

}
