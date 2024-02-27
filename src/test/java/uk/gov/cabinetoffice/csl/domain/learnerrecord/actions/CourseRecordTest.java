package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CourseRecordTest {

    private CourseRecord courseRecord;

    @BeforeEach
    public void before() {
        CourseRecord cr = new CourseRecord();
        ModuleRecord moduleRecord1 = new ModuleRecord();
        moduleRecord1.setModuleId("one");
        ModuleRecord moduleRecord2 = new ModuleRecord();
        moduleRecord2.setModuleId("two");
        ModuleRecord moduleRecord3 = new ModuleRecord();
        moduleRecord3.setModuleId("three");
        cr.setModuleRecords(List.of(moduleRecord1, moduleRecord2,
                moduleRecord3));
        courseRecord = cr;
    }

    @Test
    public void shouldUpdateExistingModuleRecord() {
        courseRecord.getModuleRecord("one").get().setState(State.IN_PROGRESS);
        ModuleRecord moduleRecord1 = new ModuleRecord();
        moduleRecord1.setModuleId("one");
        moduleRecord1.setState(State.COMPLETED);
        courseRecord.updateModuleRecord(moduleRecord1);
        assertEquals(State.COMPLETED, courseRecord.getModuleRecord("one").get().getState());
        assertEquals(3, courseRecord.getModuleRecords().size());
    }

    @Test
    public void shouldAddNewModuleRecord() {
        ModuleRecord moduleRecord4 = new ModuleRecord();
        moduleRecord4.setModuleId("four");
        moduleRecord4.setState(State.COMPLETED);
        courseRecord.updateModuleRecord(moduleRecord4);
        assertEquals(State.COMPLETED, courseRecord.getModuleRecord("four").get().getState());
        assertEquals(4, courseRecord.getModuleRecords().size());
    }
}
