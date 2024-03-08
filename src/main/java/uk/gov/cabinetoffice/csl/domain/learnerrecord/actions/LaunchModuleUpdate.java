package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.util.StringUtilService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class LaunchModuleUpdate implements IModuleRecordUpdate {

    private final StringUtilService stringUtilService;
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
        String moduleRecordUid = stringUtilService.generateRandomUuid();
        return ModuleRecordStatus.builder().uid(moduleRecordUid).state(State.IN_PROGRESS.name()).build();
    }

    @Override
    public List<PatchOp> getUpdateModuleRecordPatches(ModuleRecord moduleRecord) {
        List<PatchOp> patches = new ArrayList<>();
        patches.add(PatchOp.replacePatch("/updatedAt", LocalDateTime.now(clock).toString()));
        if (StringUtils.isBlank(moduleRecord.getUid())) {
            String moduleRecordUid = stringUtilService.generateRandomUuid();
            patches.add(PatchOp.replacePatch("uid", moduleRecordUid));
        }
        return patches;
    }

    @Override
    public String getName() {
        return "Launch module";
    }

}
