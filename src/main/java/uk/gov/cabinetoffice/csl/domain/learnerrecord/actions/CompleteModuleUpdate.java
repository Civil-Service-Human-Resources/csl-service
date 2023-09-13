package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompleteModuleUpdate implements IModuleRecordUpdate {

    private final Clock clock;
    private final Course course;
    private final Module module;

    public CompleteModuleUpdate(Clock clock, Course course, Module module) {
        this.clock = clock;
        this.course = course;
        this.module = module;
    }

    @Override
    public CourseRecordStatus getCreateCourseRecordStatus() {
        CourseRecordStatus.CourseRecordStatusBuilder builder = CourseRecordStatus
                .builder()
                .moduleRecordStatus(
                        ModuleRecordStatus.builder().state(State.COMPLETED.name()).build()
                )
                .state(State.IN_PROGRESS.name());
        Collection<Module> modules = course.getModules();
        if (modules.size() == 1 ||
                (modules.stream().filter(m -> !m.isOptional()).toList().size() == 1
                        && !module.isOptional())) {
            builder.state(State.COMPLETED.name());
        }
        return builder.build();
    }

    @Override
    public List<PatchOp> getUpdateCourseRecordPatches(CourseRecord courseRecord) {
        List<PatchOp> patches = new ArrayList<>();
        if (courseRecord.getState() == null || !courseRecord.getState().equals(State.COMPLETED)) {
            if (course.isCourseComplete(courseRecord)) {
                patches.add(PatchOp.replacePatch("state", State.COMPLETED.name()));
            } else if (!courseRecord.getState().equals(State.IN_PROGRESS)) {
                patches.add(PatchOp.replacePatch("state", State.IN_PROGRESS.name()));
            }
        }
        return patches;
    }

    @Override
    public ModuleRecordStatus getCreateModuleRecordStatus() {
        return ModuleRecordStatus.builder().state(State.COMPLETED.name()).build();
    }

    @Override
    public List<PatchOp> getUpdateModuleRecordPatches(ModuleRecord moduleRecord) {
        return List.of(
                PatchOp.replacePatch("state", State.COMPLETED.name()),
                PatchOp.replacePatch("/completionDate", LocalDateTime.now(clock).toString())
        );
    }

    @Override
    public String getName() {
        return "Launch module";
    }

}
