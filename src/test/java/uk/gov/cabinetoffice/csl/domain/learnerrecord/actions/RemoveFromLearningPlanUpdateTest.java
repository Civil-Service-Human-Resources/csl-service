package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RemoveFromLearningPlanUpdateTest {

    private final RemoveFromLearningPlanUpdate removeFromLearningPlanUpdate = new RemoveFromLearningPlanUpdate();

    @Test
    public void testCreateCourseRecord() {
        assertThrows(IncorrectStateException.class, removeFromLearningPlanUpdate::getCreateCourseRecordStatus);
    }

    @Test
    public void testUpdateNullCourseRecord() {
        CourseRecord courseRecord = new CourseRecord();
        courseRecord.setState(null);
        List<PatchOp> patches = removeFromLearningPlanUpdate.getUpdateCourseRecordPatches(new CourseRecord());
        PatchOp patch1 = patches.get(0);
        assertEquals("replace", patch1.getOp());
        assertEquals("/state", patch1.getPath());
        assertEquals("ARCHIVED", patch1.getValue());
    }

    @Test
    public void testDoNotUpdateCourseRecord() {
        CourseRecord courseRecord = new CourseRecord();
        courseRecord.setState(State.ARCHIVED);
        List<PatchOp> patches = removeFromLearningPlanUpdate.getUpdateCourseRecordPatches(courseRecord);
        assert (patches.isEmpty());
    }
}
