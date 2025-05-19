package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Result;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CancelBookingTest extends BaseEventModuleRecordActionTest<CancelBooking> {

    @Override
    protected CancelBooking buildProcessor() {
        return new CancelBooking();
    }

    @Test
    public void testCancelBooking() {
        ModuleRecord mr = generateModuleRecord();
        mr.setResult(Result.PASSED);
        mr.setCompletionDate(LocalDateTime.now());
        mr.setBookingStatus(BookingStatus.CONFIRMED);
        mr = actionUnderTest.applyUpdates(mr);
        assertEquals(State.UNREGISTERED, mr.getState());
        assertEquals(State.UNREGISTERED, mr.getState());
        assertEquals(BookingStatus.CANCELLED, mr.getBookingStatus());
        assertNull(mr.getResult());
        assertNull(mr.getCompletionDate());
    }

}
