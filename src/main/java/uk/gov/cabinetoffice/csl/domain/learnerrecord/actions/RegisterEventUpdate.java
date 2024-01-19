package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Event;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RegisterEventUpdate implements IModuleRecordUpdate {

    private final Event event;

    public RegisterEventUpdate(Event event) {
        this.event = event;
    }

    @Override
    public CourseRecordStatus getCreateCourseRecordStatus() {
        return CourseRecordStatus.builder()
                .moduleRecordStatus(getCreateModuleRecordStatus())
                .state(State.REGISTERED.name()).build();
    }

    @Override
    public List<PatchOp> getUpdateCourseRecordPatches(CourseRecord courseRecord) {
        List<PatchOp> patches = new ArrayList<>();
        if (courseRecord.getStateSafe().equals(State.NULL) ||
                !courseRecord.getStateSafe().equals(State.IN_PROGRESS)) {
            patches.add(PatchOp.replacePatch("state", State.REGISTERED.name()));
        }
        return patches;
    }

    @Override
    public String getName() {
        return "Book event";
    }

    @Override
    public ModuleRecordStatus getCreateModuleRecordStatus() {
        return ModuleRecordStatus.builder()
                .state(State.REGISTERED.name())
                .eventId(event.getId())
                .eventDate(event.getStartTime()).build();
    }

    @Override
    public List<PatchOp> getUpdateModuleRecordPatches(ModuleRecord moduleRecord) {
        return List.of(
                PatchOp.replacePatch("state", State.REGISTERED.name()),
                PatchOp.removePatch("result"),
                PatchOp.removePatch("score"),
                PatchOp.removePatch("completionDate"),
                PatchOp.replacePatch("eventId", event.getId()),
                PatchOp.replacePatch("eventDate", event.getStartTime().format(DateTimeFormatter.ISO_DATE))
        );
    }
}
