package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

public class RemoveFromLearningPlan extends CourseRecordActionProcessor {

    public RemoveFromLearningPlan(Course course, User user) {
        super(course, user, CourseRecordAction.REMOVE_FROM_LEARNING_PLAN);
    }

    @Override
    public CourseRecord applyUpdatesToCourseRecord(CourseRecord courseRecord) {
        if (!courseRecord.getState().equals(State.ARCHIVED)) {
            courseRecord.setState(State.ARCHIVED);
        }
        return courseRecord;
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        throw new IncorrectStateException("Can't remove a course from the learning plan if it doesn't have a corresponding course record");
    }

}
