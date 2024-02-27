package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;

public class LaunchModule extends ModuleRecordActionProcessor {

    public LaunchModule(CourseWithModule courseWithModule, User user) {
        super(courseWithModule, user, ModuleRecordAction.LAUNCH_MODULE);
    }

    @Override
    public CourseRecord updateCourseRecord(CourseRecord courseRecord) {
        if (courseRecord.getState().equals(State.NULL) || courseRecord.getState().equals(State.ARCHIVED)) {
            courseRecord.setState(State.IN_PROGRESS);
        }
        if (courseRecord.getModuleRecord(getModuleId()).isEmpty()) {
            ModuleRecord moduleRecord = courseRecord.getOrCreateModuleRecord(module);
            moduleRecord.setState(State.IN_PROGRESS);
        }
        return courseRecord;
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        return applyUpdatesToCourseRecord(createCourseRecord());
    }
}
