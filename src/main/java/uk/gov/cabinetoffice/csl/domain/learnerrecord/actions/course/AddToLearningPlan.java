package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Preference;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.time.LocalDateTime;

public class AddToLearningPlan extends CourseRecordActionProcessor {

    public AddToLearningPlan(UtilService utilService, Course course, User user) {
        super(utilService, course, user, CourseRecordAction.ADD_TO_LEARNING_PLAN);
    }

    @Override
    public CourseRecord applyUpdatesToCourseRecord(CourseRecord courseRecord, LocalDateTime completionDate) {
        State state = courseRecord.getModuleRecords().size() > 0 ? State.IN_PROGRESS : null;
        courseRecord.setState(state);
        courseRecord.setPreference(Preference.LIKED);
        return courseRecord;
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        return applyUpdatesToCourseRecord(createCourseRecord(), null);
    }

}
