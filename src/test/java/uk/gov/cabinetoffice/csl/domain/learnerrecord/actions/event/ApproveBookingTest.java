package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Result;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ApproveBookingTest extends BaseEventModuleRecordActionTest<ApproveBooking> {

    @Override
    protected ApproveBooking buildProcessor() {
        return new ApproveBooking(event);
    }

    @Test
    public void testApproveBooking() {
        ModuleRecord mr = generateModuleRecord();
        mr.setResult(Result.PASSED);
        mr.setCompletionDate(LocalDateTime.now());
        mr = actionUnderTest.applyUpdates(mr);
        assertEquals(State.APPROVED, mr.getState());
        assertEquals(event.getId(), mr.getEventId());
        assertEquals(event.getStartTime(), mr.getEventDate());
        assertNull(mr.getResult());
        assertNull(mr.getCompletionDate());
    }
}
