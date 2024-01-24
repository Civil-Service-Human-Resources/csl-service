package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;

import java.util.ArrayList;
import java.util.List;

public class SkipBookingUpdate implements IModuleRecordUpdate {
    @Override
    public CourseRecordStatus getCreateCourseRecordStatus() {
        throw new IncorrectStateException("Can't create a new course record when skipping an event.");
    }

    @Override
    public List<PatchOp> getUpdateCourseRecordPatches(CourseRecord courseRecord) {
        List<PatchOp> patches = new ArrayList<>();
        if (courseRecord.getStateSafe().equals(State.REGISTERED)) {
            patches.add(PatchOp.replacePatch("state", State.SKIPPED.name()));
        }
        return patches;
    }

    @Override
    public String getName() {
        return "Skip booking";
    }

    @Override
    public ModuleRecordStatus getCreateModuleRecordStatus() {
        throw new IncorrectStateException("Can't create a new module record when skipping an event.");
    }

    @Override
    public List<PatchOp> getUpdateModuleRecordPatches(ModuleRecord moduleRecord) {
        return List.of(
                PatchOp.replacePatch("state", State.SKIPPED.name()),
                PatchOp.removePatch("bookingStatus"),
                PatchOp.removePatch("result"),
                PatchOp.removePatch("score"),
                PatchOp.removePatch("completionDate")
        );
    }
}
