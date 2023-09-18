package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddToLearningPlanUpdateTest {

    private final AddToLearningPlanUpdate addToLearningPlanUpdate = new AddToLearningPlanUpdate();

    @Test
    public void testCreateCourseRecord() {
        CourseRecordStatus status = addToLearningPlanUpdate.getCreateCourseRecordStatus();
        assertEquals("LIKED", status.getPreference());
    }

    @Test
    public void testUpdateCourseRecord() {
        List<PatchOp> patches = addToLearningPlanUpdate.getUpdateCourseRecordPatches(new CourseRecord());
        PatchOp patch1 = patches.get(0);
        PatchOp patch2 = patches.get(1);
        assertEquals("replace", patch1.getOp());
        assertEquals("/preference", patch1.getPath());
        assertEquals("LIKED", patch1.getValue());
        assertEquals("remove", patch2.getOp());
        assertEquals("/state", patch2.getPath());
    }
}
