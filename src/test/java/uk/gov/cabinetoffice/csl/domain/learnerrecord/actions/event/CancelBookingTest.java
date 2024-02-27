package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Result;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CancelBookingTest extends BaseEventModuleRecordActionTest<CancelBooking> {

    @Override
    protected CancelBooking buildProcessor() {
        return new CancelBooking(courseWithModuleWithEvent, user);
    }

    @Test
    public void testCancelBooking() {
        CourseRecord cr = generateCourseRecord(true);
        cr.setState(State.REGISTERED);
        ModuleRecord mr = cr.getModuleRecord(getModuleId()).get();
        mr.setResult(Result.PASSED);
        mr.setCompletionDate(LocalDateTime.now());
        mr.setBookingStatus(BookingStatus.CONFIRMED);
        cr = actionUnderTest.applyUpdatesToCourseRecord(cr);
        assertEquals(State.UNREGISTERED, cr.getState());

        ModuleRecord moduleRecord = cr.getModuleRecord(getModuleId()).get();
        assertEquals(State.UNREGISTERED, moduleRecord.getState());
        assertEquals(BookingStatus.CANCELLED, moduleRecord.getBookingStatus());
        assertNull(moduleRecord.getResult());
        assertNull(moduleRecord.getCompletionDate());
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
}
