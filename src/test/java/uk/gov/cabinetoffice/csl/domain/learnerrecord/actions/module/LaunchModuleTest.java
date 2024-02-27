package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LaunchModuleTest extends BaseModuleRecordActionTest<LaunchModule> {

    @Override
    protected LaunchModule buildProcessor() {
        return new LaunchModule(courseWithModule, user);
    }

    @Test
    public void testLaunchModule() {
        CourseRecord cr = new CourseRecord();
        cr = actionUnderTest.applyUpdatesToCourseRecord(cr);
        assertEquals(State.IN_PROGRESS, cr.getState());
        assertEquals(State.IN_PROGRESS, cr.getModuleRecords().stream().findFirst().get().getState());
    }
}
