package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LaunchModuleTest extends BaseModuleRecordActionTest<LaunchModule> {

    @Override
    protected LaunchModule buildProcessor() {
        return new LaunchModule(utilService, courseWithModule, user);
    }

    @Test
    public void testLaunchModule() {
        CourseRecord cr = new CourseRecord();
        cr = actionUnderTest.applyUpdatesToCourseRecord(cr);
        assertEquals(State.IN_PROGRESS, cr.getState());
        assertEquals(State.IN_PROGRESS, cr.getModuleRecords().stream().findFirst().get().getState());
    }

    /**
     * When updating a course/module record combination, the processor should return a course record
     * with the single module record that was updated. This is so that the course and module record
     * can be individually sent to the learner record for updates, rather than sending the entire course
     * record with all module records.
     */
    @Test
    public void testLaunchModulemultipleModules() {
        CourseRecord cr = generateCourseRecord(1);
        ModuleRecord extraMr = generateModuleRecord();
        extraMr.setState(State.IN_PROGRESS);
        cr.updateModuleRecord(extraMr);

        cr = actionUnderTest.applyUpdatesToCourseRecord(cr);
        assertEquals(State.IN_PROGRESS, cr.getState());
        assertEquals(1, cr.getModuleRecords().size());
        assertEquals(State.IN_PROGRESS, cr.getModuleRecordAndThrowIfNotFound(getModuleId()).getState());
    }

}
