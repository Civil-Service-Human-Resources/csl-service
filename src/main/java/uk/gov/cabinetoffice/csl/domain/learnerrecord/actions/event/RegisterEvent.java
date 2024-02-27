package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;

public class RegisterEvent extends EventModuleRecordActionProcessor {

    public RegisterEvent(CourseWithModuleWithEvent courseWithModuleWithEvent, User user) {
        super(courseWithModuleWithEvent, user, EventModuleRecordAction.REGISTER_BOOKING);
    }

    @Override
    public CourseRecord updateCourseRecord(CourseRecord courseRecord) {
        if (courseRecord.getState().equals(State.NULL) ||
                !courseRecord.getState().equals(State.IN_PROGRESS)) {
            courseRecord.setState(State.REGISTERED);
        }
        ModuleRecord moduleRecord = courseRecord.getOrCreateModuleRecord(module);
        moduleRecord.setState(State.REGISTERED);
        moduleRecord.setEventId(event.getId());
        moduleRecord.setEventDate(event.getStartTime());
        moduleRecord.setResult(null);
        moduleRecord.setCompletionDate(null);
        return courseRecord;
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        return applyUpdatesToCourseRecord(createCourseRecord());
    }
}
