package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompleteBookingTest extends BaseEventModuleRecordActionTest<CompleteBooking> {

    @Override
    protected CompleteBooking buildProcessor() {
        return new CompleteBooking(this.utilService.getNowDateTime());
    }

    @Test
    public void testCompleteBooking() {
        ModuleRecord mr = generateModuleRecord();
        mr.setState(State.APPROVED);
        mr = actionUnderTest.applyUpdates(mr);
        assertEquals(State.COMPLETED, mr.getState());
    }

    @Test
    public void testCompleteBookingIncorrectState() {
        ModuleRecord mr = generateModuleRecord();
        mr.setState(State.REGISTERED);
        assertThrows(IncorrectStateException.class, () -> {
            actionUnderTest.applyUpdates(mr);
        });
    }
}
