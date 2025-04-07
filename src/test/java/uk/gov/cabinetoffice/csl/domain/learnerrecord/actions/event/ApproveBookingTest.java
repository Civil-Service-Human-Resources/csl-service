package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Result;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ApproveBookingTest extends BaseEventModuleRecordActionTest<ApproveBooking> {

    @Override
    protected ApproveBooking buildProcessor() {
        return new ApproveBooking(utilService, courseWithModuleWithEvent, user);
    }

    @Test
    public void testApproveBooking() {
        CourseRecord cr = generateCourseRecord(true);
        cr.setState(State.REGISTERED);
        ModuleRecord mr = cr.getModuleRecord(getModuleId()).get();
        mr.setResult(Result.PASSED);
        mr.setCompletionDate(LocalDateTime.now());
        cr = actionUnderTest.applyUpdatesToCourseRecord(cr, null);
        assertEquals(State.APPROVED, cr.getState());

        ModuleRecord moduleRecord = cr.getModuleRecord(getModuleId()).get();
        assertEquals(State.APPROVED, moduleRecord.getState());
        assertEquals(event.getId(), moduleRecord.getEventId());
        assertEquals(event.getStartTime(), moduleRecord.getEventDate());
        assertNull(moduleRecord.getResult());
        assertNull(moduleRecord.getCompletionDate());
    }
}
