package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.util.ArrayList;
import java.util.List;

@Component
public class RemoveFromLearningPlanUpdate implements ICourseRecordUpdate {

    @Override
    public CourseRecordStatus getCreateCourseRecordStatus() {
        throw new IncorrectStateException("Can't remove a course from the learning plan if it doesn't have a corresponding course record");
    }

    @Override
    public List<PatchOp> getUpdateCourseRecordPatches(CourseRecord courseRecord) {
        List<PatchOp> patches = new ArrayList<>();
        if (courseRecord.getState() != null && !courseRecord.getState().equals(State.ARCHIVED)) {
            patches.add(PatchOp.replacePatch("state", State.ARCHIVED.name()));
        }
        return patches;
    }

    @Override
    public String getName() {
        return "Remove from Learning plan";
    }
}
