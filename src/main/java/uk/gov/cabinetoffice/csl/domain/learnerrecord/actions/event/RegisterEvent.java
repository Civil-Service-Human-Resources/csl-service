package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.util.UtilService;

public class RegisterEvent extends EventModuleRecordActionProcessor {

    public RegisterEvent(UtilService utilService, CourseWithModuleWithEvent courseWithModuleWithEvent, User user) {
        super(utilService, courseWithModuleWithEvent, user, EventModuleRecordAction.REGISTER_BOOKING);
    }

    @Override
    public ModuleRecord applyUpdatesToModuleRecord(ModuleRecord moduleRecord) {
        moduleRecord.setState(State.REGISTERED);
        moduleRecord.setEventId(event.getId());
        moduleRecord.setEventDate(event.getStartTime());
        moduleRecord.setResult(null);
        moduleRecord.setCompletionDate(null);
        return moduleRecord;
    }

}
