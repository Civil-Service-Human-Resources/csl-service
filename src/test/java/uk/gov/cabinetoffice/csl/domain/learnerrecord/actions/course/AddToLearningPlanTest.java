package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Preference;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddToLearningPlanTest extends BaseCourseRecordActionTest<AddToLearningPlan> {

    @Override
    protected AddToLearningPlan buildProcessor() {
        return new AddToLearningPlan(utilService, course, user);
    }

    @Test
    public void testAddToLearningPlan() {
        CourseRecord cr = new CourseRecord();
        cr.setState(State.ARCHIVED);
        cr = actionUnderTest.applyUpdatesToCourseRecord(cr);
        assertEquals(Preference.LIKED, cr.getPreference());
        assertEquals(State.NULL, cr.getState());
    }

    @Test
    public void testAddToLearningPlanInProgress() {
        CourseRecord cr = this.generateCourseRecord(true);
        cr.setState(State.ARCHIVED);
        cr = actionUnderTest.applyUpdatesToCourseRecord(cr);
        assertEquals(Preference.LIKED, cr.getPreference());
        assertEquals(State.IN_PROGRESS, cr.getState());
    }
}
