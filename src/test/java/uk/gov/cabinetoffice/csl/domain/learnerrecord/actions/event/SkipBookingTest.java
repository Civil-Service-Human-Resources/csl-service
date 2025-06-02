package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Result;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SkipBookingTest extends BaseEventModuleRecordActionTest<SkipBooking> {

    @Override
    protected SkipBooking buildProcessor() {
        return new SkipBooking();
    }

    @Test
    public void testCancelBooking() {
        ModuleRecord mr = generateModuleRecord();
        mr.setState(State.APPROVED);
        mr.setResult(Result.PASSED);
        mr.setCompletionDate(LocalDateTime.now());
        mr = actionUnderTest.applyUpdates(mr);
        assertEquals(State.SKIPPED, mr.getState());
        assertNull(mr.getBookingStatus());
        assertNull(mr.getResult());
    }

    @Test
    public void testCompleteBookingIncorrectState() {
        ModuleRecord mr = generateModuleRecord();
        mr.setState(State.REGISTERED);
        assertThrows(IncorrectStateException.class, () -> actionUnderTest.applyUpdates(mr));
    }
}
