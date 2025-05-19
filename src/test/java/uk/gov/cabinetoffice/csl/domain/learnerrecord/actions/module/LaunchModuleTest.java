package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LaunchModuleTest extends BaseModuleRecordActionTest<LaunchModule> {

    @Override
    protected LaunchModule buildProcessor() {
        return new LaunchModule();
    }

    @Test
    public void testLaunchModule() {
        ModuleRecord mr = new ModuleRecord();
        mr = actionUnderTest.applyUpdates(mr);
        assertEquals(State.IN_PROGRESS, mr.getState());
    }

}
