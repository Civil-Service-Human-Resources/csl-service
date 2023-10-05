package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompleteModuleUpdateTest {

    private CompleteModuleUpdate completeModuleUpdate;
    private final Course course = new Course();

    @Test
    public void testCreateCourseRecord() {
        course.setModules(Collections.emptyList());
        completeModuleUpdate = new CompleteModuleUpdate(null, course, null);
        CourseRecordStatus status = completeModuleUpdate.getCreateCourseRecordStatus();
        assertEquals("IN_PROGRESS", status.getState());
        assertEquals("COMPLETED", status.getModuleRecordStatus().getState());
    }

    @Test
    public void testCreateCompletedCourseRecord() {
        Module module = new Module();
        course.setModules(List.of(module));
        completeModuleUpdate = new CompleteModuleUpdate(null, course, module);
        CourseRecordStatus status = completeModuleUpdate.getCreateCourseRecordStatus();
        assertEquals("COMPLETED", status.getState());
        assertEquals("COMPLETED", status.getModuleRecordStatus().getState());
    }

    @Test
    public void testCreateCompletedCourseRecordMultipleModules() {
        Module module1 = new Module();
        module1.setOptional(false);
        Module module2 = new Module();
        module2.setOptional(true);
        course.setModules(List.of(module1, module2));
        completeModuleUpdate = new CompleteModuleUpdate(null, course, module1);
        CourseRecordStatus status = completeModuleUpdate.getCreateCourseRecordStatus();
        assertEquals("COMPLETED", status.getState());
        assertEquals("COMPLETED", status.getModuleRecordStatus().getState());
    }

    @Test
    public void testUpdateCourseRecordCourseAlreadyComplete() {
        CourseRecord courseRecord = new CourseRecord();
        courseRecord.setState(State.COMPLETED);
        completeModuleUpdate = new CompleteModuleUpdate(null, null, null);
        List<PatchOp> patches = completeModuleUpdate.getUpdateCourseRecordPatches(courseRecord);
        assert (patches.isEmpty());
    }

    @Test
    public void testUpdateCourseRecordCourseComplete() {
        Course course = mock(Course.class);
        CourseRecord courseRecord = new CourseRecord();
        courseRecord.setState(State.IN_PROGRESS);
        when(course.isCourseComplete(courseRecord)).thenReturn(true);
        completeModuleUpdate = new CompleteModuleUpdate(null, course, null);
        List<PatchOp> patches = completeModuleUpdate.getUpdateCourseRecordPatches(courseRecord);
        PatchOp patch1 = patches.get(0);
        assertEquals("replace", patch1.getOp());
        assertEquals("/state", patch1.getPath());
        assertEquals("COMPLETED", patch1.getValue());
    }

    @Test
    public void testUpdateCourseRecordCourseNotComplete() {
        Course course = mock(Course.class);
        CourseRecord courseRecord = new CourseRecord();
        courseRecord.setState(State.ARCHIVED);
        when(course.isCourseComplete(courseRecord)).thenReturn(false);
        completeModuleUpdate = new CompleteModuleUpdate(null, course, null);
        List<PatchOp> patches = completeModuleUpdate.getUpdateCourseRecordPatches(courseRecord);
        PatchOp patch1 = patches.get(0);
        assertEquals("replace", patch1.getOp());
        assertEquals("/state", patch1.getPath());
        assertEquals("IN_PROGRESS", patch1.getValue());
    }

    @Test
    public void testCreateModuleRecord() {
        completeModuleUpdate = new CompleteModuleUpdate(null, null, null);
        ModuleRecordStatus status = completeModuleUpdate.getCreateModuleRecordStatus();
        assertEquals("COMPLETED", status.getState());
    }

    @Test
    public void testUpdateModuleRecord() {
        Clock clock = Clock.fixed(Instant.parse("2023-01-01T10:00:00.000Z"), ZoneId.of("Europe/London"));
        completeModuleUpdate = new CompleteModuleUpdate(clock, null, null);
        List<PatchOp> patches = completeModuleUpdate.getUpdateModuleRecordPatches(null);
        PatchOp patch1 = patches.get(0);
        assertEquals("replace", patch1.getOp());
        assertEquals("/state", patch1.getPath());
        assertEquals("COMPLETED", patch1.getValue());
        PatchOp patch2 = patches.get(1);
        assertEquals("replace", patch2.getOp());
        assertEquals("/completionDate", patch2.getPath());
        assertEquals("2023-01-01T10:00", patch2.getValue());
    }
}
