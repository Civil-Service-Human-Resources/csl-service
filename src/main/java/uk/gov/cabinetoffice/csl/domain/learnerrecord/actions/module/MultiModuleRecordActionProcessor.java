package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.MultiCourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;

public class MultiModuleRecordActionProcessor extends ModuleRecordActionProcessor {

    private final MultiCourseRecordAction actionTypes;

    public MultiModuleRecordActionProcessor(CourseWithModule courseWithModule, User user, MultiCourseRecordAction actionTypes) {
        super(courseWithModule, user, actionTypes);
        this.actionTypes = actionTypes;
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        return this.applyUpdatesToCourseRecord(createCourseRecord());
    }

    @Override
    protected CourseRecord updateCourseRecord(CourseRecord courseRecord) {
        for (ICourseRecordAction action : actionTypes.getActions()) {
            courseRecord = action.applyUpdatesToCourseRecord(courseRecord);
            messages.addAll(action.getMessages());
        }
        return courseRecord;
    }
}
