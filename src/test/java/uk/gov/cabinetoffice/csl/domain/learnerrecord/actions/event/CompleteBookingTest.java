package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.service.messaging.model.CourseCompletionMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompleteBookingTest extends BaseEventModuleRecordActionTest<CompleteBooking> {

    @Override
    protected CompleteBooking buildProcessor() {
        return new CompleteBooking(utilService, courseWithModuleWithEvent, user);
    }

    @Test
    public void testCompleteBooking() {
        CourseRecord cr = generateCourseRecord(true);
        cr.setState(State.APPROVED);
        ModuleRecord mr = cr.getModuleRecord(getModuleId()).get();
        mr.setState(State.APPROVED);
        cr = actionUnderTest.applyUpdatesToModuleRecord(cr);
        assertEquals(State.COMPLETED, cr.getState());
        ModuleRecord moduleRecord = cr.getModuleRecord(getModuleId()).get();
        assertEquals(State.COMPLETED, moduleRecord.getState());

        assertTrue(actionUnderTest.getMessages().stream().findFirst().isPresent());
        CourseCompletionMessage courseCompletionMessage = (CourseCompletionMessage) actionUnderTest.getMessages().stream().findFirst().get();
        assertEquals(getUserId(), courseCompletionMessage.getUserId());
        assertEquals(getUserEmail(), courseCompletionMessage.getUserEmail());
        assertEquals(getCourseTitle(), courseCompletionMessage.getCourseTitle());
        assertEquals(getCourseId(), courseCompletionMessage.getCourseId());
        assertEquals(getOrganisationalUnit().getId().intValue(), courseCompletionMessage.getOrganisationId());
        assertEquals(getProfession().getId().intValue(), courseCompletionMessage.getProfessionId());
        assertEquals(getGrade().getId().intValue(), courseCompletionMessage.getGradeId());
    }

    @Test
    public void testCompleteBookingNoModuleRecord() {
        CourseRecord cr = generateCourseRecord(false);
        assertThrows(IncorrectStateException.class, () -> {
            actionUnderTest.updateCourseRecord(cr);
        });
    }

    @Test
    public void testCompleteBookingNoCourseRecord() {
        assertThrows(IncorrectStateException.class, () -> {
            actionUnderTest.generateNewModuleRecord();
        });
    }

    @Test
    public void testCompleteBookingIncorrectState() {
        CourseRecord cr = generateCourseRecord(true);
        cr.setState(State.REGISTERED);
        ModuleRecord mr = cr.getModuleRecord(getModuleId()).get();
        mr.setState(State.REGISTERED);
        assertThrows(IncorrectStateException.class, () -> {
            actionUnderTest.updateCourseRecord(cr);
        });
    }
}
