package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
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
        return new RegisterEvent(utilService, courseWithModuleWithEvent, user);
    }

    @Test
    public void testRegisterBooking() {
        CourseRecord cr = generateCourseRecord(true);
        cr.setState(State.NULL);
        ModuleRecord mr = cr.getModuleRecord(getModuleId()).get();
        mr.setResult(Result.PASSED);
        mr.setCompletionDate(LocalDateTime.now());
        mr.setBookingStatus(BookingStatus.CONFIRMED);
        cr = actionUnderTest.applyUpdatesToCourseRecord(cr, null);
        assertEquals(State.REGISTERED, cr.getState());

        ModuleRecord moduleRecord = cr.getModuleRecord(getModuleId()).get();
        assertEquals(State.REGISTERED, moduleRecord.getState());
        assertEquals(event.getId(), moduleRecord.getEventId());
        assertEquals(event.getStartTime(), moduleRecord.getEventDate());
        assertNull(moduleRecord.getResult());
        assertNull(moduleRecord.getCompletionDate());
    }

}
