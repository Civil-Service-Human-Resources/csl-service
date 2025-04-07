package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.MultiCourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.time.LocalDateTime;

public class MultiModuleRecordActionProcessor extends ModuleRecordActionProcessor {

    private final MultiCourseRecordAction actionTypes;

    public MultiModuleRecordActionProcessor(UtilService utilService, CourseWithModule courseWithModule,
                                            User user, MultiCourseRecordAction actionTypes) {
        super(utilService, courseWithModule, user, actionTypes);
        this.actionTypes = actionTypes;
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        return this.applyUpdatesToCourseRecord(createCourseRecord(), null);
    }

    @Override
    protected CourseRecord updateCourseRecord(CourseRecord courseRecord, LocalDateTime completionDate) {
        for (ICourseRecordAction action : actionTypes.getActions()) {
            courseRecord = action.applyUpdatesToCourseRecord(courseRecord, completionDate);
            action.getMessages().forEach(this::addMessage);
            action.getEmails().forEach(this::addEmail);
        }
        return courseRecord;
    }
}
