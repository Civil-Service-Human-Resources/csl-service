package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.ModuleRecordActionProcessor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Event;

public abstract class EventModuleRecordActionProcessor extends ModuleRecordActionProcessor {

    protected final Event event;

    protected EventModuleRecordActionProcessor(CourseWithModuleWithEvent courseWithModuleWithEvent, User user, ICourseRecordActionType actionType) {
        super(courseWithModuleWithEvent, user, actionType);
        this.event = courseWithModuleWithEvent.getEvent();
    }

    @Override
    public String toString() {
        return String.format("%s | Event ID: %s", super.toString(), event.getId());
    }

}
