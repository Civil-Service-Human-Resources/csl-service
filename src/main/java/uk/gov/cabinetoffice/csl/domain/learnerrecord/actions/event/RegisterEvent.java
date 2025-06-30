package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;

public class RegisterEvent extends EventModuleAction {

    public RegisterEvent(Event event) {
        super(event);
    }

    @Override
    public ModuleRecord applyUpdates(ModuleRecord moduleRecord) {
        moduleRecord.setState(State.REGISTERED);
        moduleRecord.setEventId(event.getId());
        moduleRecord.setEventDate(event.getStartTime());
        moduleRecord.setResult(null);
        moduleRecord.setCompletionDate(null);
        return moduleRecord;
    }

    @Override
    public EventModuleRecordAction getAction() {
        return EventModuleRecordAction.REGISTER_BOOKING;
    }

    @Override
    public boolean canCreateRecord() {
        return true;
    }

}
