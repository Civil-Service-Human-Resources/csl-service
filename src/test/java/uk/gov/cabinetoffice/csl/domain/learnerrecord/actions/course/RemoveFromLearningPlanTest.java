package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoveFromLearningPlanTest extends BaseCourseRecordActionTest<RemoveFromLearningPlan> {

    @Override
    protected RemoveFromLearningPlan buildProcessor() {
        return new RemoveFromLearningPlan(utilService, course, user);
    }

    @Test
    public void testRemoveFromLearningPlan() {
        CourseRecord cr = actionUnderTest.applyUpdatesToCourseRecord(generateCourseRecord(false));
        assertEquals(State.ARCHIVED, cr.getState());
    }
}
