package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.service.messaging.model.CourseCompletionMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompleteModuleTest extends BaseModuleRecordActionTest<CompleteModule> {

    @Override
    protected CompleteModule buildProcessor() {
        return new CompleteModule(courseWithModule, user);
    }

    @Test
    public void testCompleteModule() {
        CourseRecord cr = generateCourseRecord(true);
        cr = actionUnderTest.applyUpdatesToCourseRecord(cr);
        assertEquals(State.COMPLETED, cr.getState());
        assertEquals(State.COMPLETED, cr.getModuleRecord(getModuleId()).get().getState());
        assertTrue(actionUnderTest.getMessages().stream().findFirst().isPresent());
        CourseCompletionMessage courseCompletionMessage = (CourseCompletionMessage) actionUnderTest.getMessages().stream().findFirst().get();
        assertEquals(getUserId(), courseCompletionMessage.getUserId());
        assertEquals(getUseremail(), courseCompletionMessage.getUserEmail());
        assertEquals(getCourseTitle(), courseCompletionMessage.getCourseTitle());
        assertEquals(getCourseId(), courseCompletionMessage.getCourseId());
        assertEquals(getOrganisationalUnit().getId().intValue(), courseCompletionMessage.getOrganisationId());
        assertEquals(getProfession().getId().intValue(), courseCompletionMessage.getProfessionId());
        assertEquals(getGrade().getId().intValue(), courseCompletionMessage.getGradeId());
    }

    @Test
    public void testCompleteRequiredLearningModule() {
        CourseRecord cr = generateCourseRecord(true);
        cr = actionUnderTest.applyUpdatesToCourseRecord(cr);
        assertEquals(State.COMPLETED, cr.getState());
        assertEquals(State.COMPLETED, cr.getModuleRecord(getModuleId()).get().getState());
        assertTrue(actionUnderTest.getMessages().stream().findFirst().isPresent());
        CourseCompletionMessage courseCompletionMessage = (CourseCompletionMessage) actionUnderTest.getMessages().stream().findFirst().get();
        assertEquals(getUserId(), courseCompletionMessage.getUserId());
        assertEquals(getUseremail(), courseCompletionMessage.getUserEmail());
        assertEquals(getCourseTitle(), courseCompletionMessage.getCourseTitle());
        assertEquals(getCourseId(), courseCompletionMessage.getCourseId());
        assertEquals(getOrganisationalUnit().getId().intValue(), courseCompletionMessage.getOrganisationId());
        assertEquals(getProfession().getId().intValue(), courseCompletionMessage.getProfessionId());
        assertEquals(getGrade().getId().intValue(), courseCompletionMessage.getGradeId());
    }
}
