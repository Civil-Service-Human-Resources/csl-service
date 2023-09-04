package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.apache.commons.lang3.StringUtils;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.util.StringUtilService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LaunchModuleUpdate implements ModuleRecordUpdate {

    private final StringUtilService stringUtilService;
    private final boolean courseIsRequired;
    private final Clock clock;

    public LaunchModuleUpdate(StringUtilService stringUtilService, boolean courseIsRequired, Clock clock) {

        this.stringUtilService = stringUtilService;
        this.courseIsRequired = courseIsRequired;
        this.clock = clock;
    }

    @Override
    public CourseRecordStatus getCreateCourseRecordStatus() {
        String moduleRecordUid = stringUtilService.generateRandomUuid();
        return CourseRecordStatus.builder().state(State.IN_PROGRESS.name())
                .isRequired(courseIsRequired).moduleRecordStatus(
                        ModuleRecordStatus.builder().uid(moduleRecordUid).state(State.IN_PROGRESS.name()).build()
                ).build();
    }

    @Override
    public List<PatchOp> getUpdateCourseRecordPatches(CourseRecord courseRecord) {
        List<PatchOp> patches = new ArrayList<>();
        if (courseRecord.getState() == null || courseRecord.getState().equals(State.ARCHIVED)) {
            //Update the course record status if it is null or ARCHIVED
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
