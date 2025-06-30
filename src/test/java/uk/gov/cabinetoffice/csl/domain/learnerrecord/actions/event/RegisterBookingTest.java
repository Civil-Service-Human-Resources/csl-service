package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Result;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RegisterBookingTest extends BaseEventModuleRecordActionTest<RegisterEvent> {

    @Override
    protected RegisterEvent buildProcessor() {
        return new RegisterEvent(event);
    }

    @Test
    public void testRegisterBooking() {
        ModuleRecord mr = generateModuleRecord();
        mr.setResult(Result.PASSED);
        mr.setCompletionDate(LocalDateTime.now());
        mr.setBookingStatus(BookingStatus.CONFIRMED);
        mr = actionUnderTest.applyUpdates(mr);
        assertEquals(State.REGISTERED, mr.getState());
        assertEquals(event.getId(), mr.getEventId());
        assertEquals(event.getStartTime(), mr.getEventDate());
        assertNull(mr.getResult());
        assertNull(mr.getCompletionDate());
    }

}
