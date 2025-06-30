package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.IModuleAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;

public abstract class EventModuleAction implements IModuleAction {

    protected final Event event;

    protected EventModuleAction(Event event) {
        this.event = event;
    }

    public abstract EventModuleRecordAction getAction();
}
