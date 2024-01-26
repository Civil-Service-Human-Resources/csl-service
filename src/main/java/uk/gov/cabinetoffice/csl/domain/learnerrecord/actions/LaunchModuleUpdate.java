package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.AllArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class LaunchModuleUpdate implements IModuleRecordUpdate {

    private final boolean courseIsRequired;
    private final Clock clock;

    @Override
    public CourseRecordStatus getCreateCourseRecordStatus() {
        return CourseRecordStatus.builder().state(State.IN_PROGRESS.name())
                .isRequired(courseIsRequired).moduleRecordStatus(getCreateModuleRecordStatus()).build();
    }

    @Override
    public List<PatchOp> getUpdateCourseRecordPatches(CourseRecord courseRecord) {
        List<PatchOp> patches = new ArrayList<>();
        if (courseRecord.getState() == null || courseRecord.getState().equals(State.ARCHIVED)) {
            patches.add(PatchOp.replacePatch("state", State.IN_PROGRESS.name()));
        }
        return patches;
    }

    @Override
    public ModuleRecordStatus getCreateModuleRecordStatus() {
        return ModuleRecordStatus.builder().state(State.IN_PROGRESS.name()).build();
    }

    @Override
    public List<PatchOp> getUpdateModuleRecordPatches(ModuleRecord moduleRecord) {
        return List.of(PatchOp.replacePatch("/updatedAt", LocalDateTime.now(clock).toString()));
    }

    @Override
    public String getName() {
        return "Launch module";
    }

}
