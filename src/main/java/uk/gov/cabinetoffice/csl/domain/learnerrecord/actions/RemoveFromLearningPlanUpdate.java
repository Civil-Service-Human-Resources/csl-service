package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.util.Collections;
import java.util.List;

@Component
public class RemoveFromLearningPlanUpdate implements CourseRecordUpdate {

    @Override
    public CourseRecordStatus getCreateCourseRecordStatus() {
        throw new IncorrectStateException("Can't remove a course from the learning plan if it doesn't have a corresponding course record");
    }

    @Override
    public List<PatchOp> getUpdateCourseRecordPatches() {
        return Collections.singletonList(PatchOp.replacePatch("state", State.ARCHIVED.name()));
    }

    @Override
    public String getName() {
        return "Remove from Learning plan";
    }
}
