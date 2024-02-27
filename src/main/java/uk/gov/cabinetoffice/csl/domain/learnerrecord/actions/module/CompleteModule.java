package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;

public class CompleteModule extends ModuleRecordActionProcessor {

    public CompleteModule(CourseWithModule courseWithModule, User user) {
        super(courseWithModule, user, ModuleRecordAction.COMPLETE_MODULE);
    }

    @Override
    public CourseRecord updateCourseRecord(CourseRecord courseRecord) {
        ModuleRecord moduleRecord = courseRecord.getOrCreateModuleRecord(module);
        if (willModuleCompletionCompleteCourse(courseRecord)) {
            courseRecord.setState(State.COMPLETED);
            messages.add(generateCompletionMessage());
        } else if (courseRecord.getState().equals(State.NULL) ||
                courseRecord.getState().equals(State.ARCHIVED)) {
            courseRecord.setState(State.IN_PROGRESS);
        }
        moduleRecord.setState(State.COMPLETED);
        return courseRecord;
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        return applyUpdatesToCourseRecord(createCourseRecord());
    }
}
