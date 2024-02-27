package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Preference;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

public class RemoveFromSuggestions extends CourseRecordActionProcessor {

    public RemoveFromSuggestions(Course course, User user) {
        super(course, user, CourseRecordAction.REMOVE_FROM_SUGGESTIONS);
    }

    @Override
    public CourseRecord applyUpdatesToCourseRecord(CourseRecord courseRecord) {
        throw new IncorrectStateException("Can't remove a course from suggestions when there is a course record present");
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        CourseRecord courseRecord = createCourseRecord();
        courseRecord.setPreference(Preference.DISLIKED);
        return courseRecord;
    }

}
