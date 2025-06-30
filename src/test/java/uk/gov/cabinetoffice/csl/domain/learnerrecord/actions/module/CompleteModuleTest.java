package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompleteModuleTest extends BaseModuleRecordActionTest<CompleteModule> {

    @Override
    protected CompleteModule buildProcessor() {
        return new CompleteModule(this.utilService.getNowDateTime());
    }

    @Test
    public void testCompleteModule() {
        ModuleRecord mr = generateModuleRecord();
        mr = actionUnderTest.applyUpdates(mr);
        assertEquals(State.COMPLETED, mr.getState());
    }
    
}
