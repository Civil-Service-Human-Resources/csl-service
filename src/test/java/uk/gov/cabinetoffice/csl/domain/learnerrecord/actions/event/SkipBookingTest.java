package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Result;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SkipBookingTest extends BaseEventModuleRecordActionTest<SkipBooking> {

    @Override
    protected SkipBooking buildProcessor() {
        return new SkipBooking(utilService, courseWithModuleWithEvent, user);
    }

    @Test
    public void testCancelBooking() {
        CourseRecord cr = generateCourseRecord(true);
        cr.setState(State.APPROVED);
        ModuleRecord mr = cr.getModuleRecord(getModuleId()).get();
        mr.setState(State.APPROVED);
        mr.setResult(Result.PASSED);
        mr.setCompletionDate(LocalDateTime.now());
        cr = actionUnderTest.applyUpdatesToCourseRecord(cr);
        assertEquals(State.SKIPPED, cr.getState());
        ModuleRecord moduleRecord = cr.getModuleRecord(getModuleId()).get();
        assertEquals(State.SKIPPED, moduleRecord.getState());
        assertNull(moduleRecord.getBookingStatus());
        assertNull(moduleRecord.getResult());
    }

    @Test
    public void testCancelBookingNoModuleRecord() {
        CourseRecord cr = generateCourseRecord(false);
        assertThrows(IncorrectStateException.class, () -> {
            actionUnderTest.updateCourseRecord(cr);
        });
    }

    @Test
    public void testCancelBookingNoCourseRecord() {
        assertThrows(IncorrectStateException.class, () -> {
            actionUnderTest.generateNewCourseRecord();
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
