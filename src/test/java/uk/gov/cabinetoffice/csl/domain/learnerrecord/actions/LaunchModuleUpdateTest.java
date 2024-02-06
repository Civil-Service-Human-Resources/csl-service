package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LaunchModuleUpdateTest {

    private LaunchModuleUpdate launchModuleUpdate;
    private final Course course = new Course();

    @Test
    public void testCreateCourseRecord() {
        course.setModules(Collections.emptyList());
        launchModuleUpdate = new LaunchModuleUpdate(true, null);
        CourseRecordStatus status = launchModuleUpdate.getCreateCourseRecordStatus();
        assertEquals("IN_PROGRESS", status.getState());
        assertEquals(true, status.getIsRequired());
        assertEquals("IN_PROGRESS", status.getModuleRecordStatus().getState());
    }

    @Test
    public void testUpdateCourseRecordNoPatches() {
        CourseRecord courseRecord = new CourseRecord();
        courseRecord.setState(State.COMPLETED);
        launchModuleUpdate = new LaunchModuleUpdate(true, null);
        List<PatchOp> patches = launchModuleUpdate.getUpdateCourseRecordPatches(courseRecord);
        assert (patches.isEmpty());
    }

    @Test
    public void testUpdateCourseRecordCourseArchived() {
        CourseRecord courseRecord = new CourseRecord();
        courseRecord.setState(State.ARCHIVED);
        launchModuleUpdate = new LaunchModuleUpdate(true, null);
        List<PatchOp> patches = launchModuleUpdate.getUpdateCourseRecordPatches(courseRecord);
        PatchOp patch1 = patches.get(0);
        assertEquals("replace", patch1.getOp());
        assertEquals("/state", patch1.getPath());
        assertEquals("IN_PROGRESS", patch1.getValue());
    }

    @Test
    public void testCreateModuleRecord() {
        launchModuleUpdate = new LaunchModuleUpdate(true, null);
        ModuleRecordStatus status = launchModuleUpdate.getCreateModuleRecordStatus();
        assertEquals("IN_PROGRESS", status.getState());
    }

    @Test
    public void testUpdateModuleRecord() {
        Clock clock = Clock.fixed(Instant.parse("2023-01-01T10:00:00.000Z"), ZoneId.of("Europe/London"));
        launchModuleUpdate = new LaunchModuleUpdate(true, clock);
        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setUid(null);
        List<PatchOp> patches = launchModuleUpdate.getUpdateModuleRecordPatches(moduleRecord);
        PatchOp patch1 = patches.get(0);
        assertEquals("replace", patch1.getOp());
        assertEquals("/updatedAt", patch1.getPath());
        assertEquals("2023-01-01T10:00", patch1.getValue());
    }

    @Test
    public void testUpdateModuleRecordExistingUid() {
        Clock clock = Clock.fixed(Instant.parse("2023-01-01T10:00:00.000Z"), ZoneId.of("Europe/London"));
        launchModuleUpdate = new LaunchModuleUpdate(true, clock);
        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setUid("test");
        List<PatchOp> patches = launchModuleUpdate.getUpdateModuleRecordPatches(moduleRecord);
        PatchOp patch1 = patches.get(0);
        assertEquals("replace", patch1.getOp());
        assertEquals("/updatedAt", patch1.getPath());
        assertEquals("2023-01-01T10:00", patch1.getValue());
    }
}
