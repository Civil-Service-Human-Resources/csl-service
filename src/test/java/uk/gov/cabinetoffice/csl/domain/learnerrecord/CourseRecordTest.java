package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.error.RecordNotFoundException;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseRecordTest extends TestDataService {
    @Test
    public void testUpdateModuleRecords() {
        CourseRecord courseRecord = generateCourseRecord(3);
        ModuleRecord moduleRecord = generateModuleRecord();
        moduleRecord.setModuleId(getModuleId() + 2);
        moduleRecord.setState(State.COMPLETED);
        moduleRecord.setUpdatedAt(LocalDateTime.MAX);
        moduleRecord.setCompletionDate(LocalDateTime.MAX);
        courseRecord.updateModuleRecord(moduleRecord);

        assertEquals(3, courseRecord.getModuleRecords().size());
        courseRecord.getModuleRecord(getModuleId() + 2)
                .ifPresentOrElse(mr -> {
                    assertEquals(State.COMPLETED, mr.getState());
                    assertEquals(LocalDateTime.MAX, mr.getUpdatedAt());
                    assertEquals(LocalDateTime.MAX, mr.getCompletionDate());
                }, () -> {
                    throw new RecordNotFoundException("");
                });
    }

    @Test
    public void testAddModuleRecords() {
        CourseRecord courseRecord = generateCourseRecord(3);
        ModuleRecord moduleRecord = generateModuleRecord();
        moduleRecord.setModuleId(getModuleId() + 4);
        moduleRecord.setState(State.COMPLETED);
        moduleRecord.setUpdatedAt(LocalDateTime.MAX);
        moduleRecord.setCompletionDate(LocalDateTime.MAX);
        courseRecord.updateModuleRecord(moduleRecord);

        assertEquals(4, courseRecord.getModuleRecords().size());
        courseRecord.getModuleRecord(getModuleId() + 4)
                .ifPresentOrElse(mr -> {
                    assertEquals(State.COMPLETED, mr.getState());
                    assertEquals(LocalDateTime.MAX, mr.getUpdatedAt());
                    assertEquals(LocalDateTime.MAX, mr.getCompletionDate());
                }, () -> {
                    throw new RecordNotFoundException("");
                });
    }

    @Test
    public void testUpdateCourseRecord() {
        CourseRecord recordToUpdate = generateCourseRecord(3);

        ModuleRecord moduleRecord = generateModuleRecord();
        moduleRecord.setModuleId(getModuleId() + 2);
        moduleRecord.setState(State.COMPLETED);
        moduleRecord.setUpdatedAt(LocalDateTime.MAX);
        moduleRecord.setCompletionDate(LocalDateTime.MAX);

        ModuleRecord moduleRecord2 = generateModuleRecord();
        moduleRecord2.setModuleId(getModuleId() + 4);
        moduleRecord2.setState(State.IN_PROGRESS);
        moduleRecord2.setUpdatedAt(LocalDateTime.MAX);

        CourseRecord updates = generateCourseRecord(0);
        updates.setState(State.IN_PROGRESS);
        updates.setLastUpdated(LocalDateTime.MAX);
        updates.setModuleRecords(List.of(moduleRecord, moduleRecord2));

        recordToUpdate.update(updates);

        assertEquals(4, recordToUpdate.getModuleRecords().size());
        assertEquals(State.IN_PROGRESS, recordToUpdate.getState());
        assertEquals(LocalDateTime.MAX, recordToUpdate.getLastUpdated());
        recordToUpdate.getModuleRecord(getModuleId() + 4)
                .ifPresentOrElse(mr -> {
                    assertEquals(State.IN_PROGRESS, mr.getState());
                    assertEquals(LocalDateTime.MAX, mr.getUpdatedAt());
                }, () -> {
                    throw new RecordNotFoundException("");
                });
        recordToUpdate.getModuleRecord(getModuleId() + 2)
                .ifPresentOrElse(mr -> {
                    assertEquals(State.COMPLETED, mr.getState());
                    assertEquals(LocalDateTime.MAX, mr.getUpdatedAt());
                    assertEquals(LocalDateTime.MAX, mr.getCompletionDate());
                }, () -> {
                    throw new RecordNotFoundException("");
                });
    }
}
