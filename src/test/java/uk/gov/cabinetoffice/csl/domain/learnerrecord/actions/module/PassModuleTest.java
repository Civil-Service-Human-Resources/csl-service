package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Result;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PassModuleTest extends BaseModuleRecordActionTest<PassModule> {

    @Override
    protected PassModule buildProcessor() {
        return new PassModule(utilService, courseWithModule, user);
    }

    @Test
    public void testPassModule() {
        CourseRecord cr = generateCourseRecord(true);
        cr.setState(State.IN_PROGRESS);
        cr = actionUnderTest.applyUpdatesToCourseRecord(cr);
        ModuleRecord moduleRecord = cr.getModuleRecord(getModuleId()).get();
        assertEquals(Result.PASSED, moduleRecord.getResult());
    }
}
