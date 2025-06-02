package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;

public class ApproveBooking extends EventModuleAction {

    public ApproveBooking(Event event) {
        super(event);
    }

    @Override
    public ModuleRecord applyUpdates(ModuleRecord moduleRecord) {
        moduleRecord.setState(State.APPROVED);
        moduleRecord.setEventId(event.getId());
        moduleRecord.setEventDate(event.getStartTime());
        moduleRecord.setResult(null);
        moduleRecord.setCompletionDate(null);
        return moduleRecord;
    }

    @Override
    public EventModuleRecordAction getAction() {
        return EventModuleRecordAction.APPROVE_BOOKING;
    }

    @Override
    public boolean canCreateRecord() {
        return true;
    }
}
