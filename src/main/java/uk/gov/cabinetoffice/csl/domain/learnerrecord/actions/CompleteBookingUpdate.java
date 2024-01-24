package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.AllArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CompleteBookingUpdate implements IModuleRecordUpdate {

    private final Course course;
    private final Clock clock;

    @Override
    public CourseRecordStatus getCreateCourseRecordStatus() {
        throw new IncorrectStateException("Can't create a new course record when completing an event.");
    }

    @Override
    public List<PatchOp> getUpdateCourseRecordPatches(CourseRecord courseRecord) {
        List<PatchOp> patches = new ArrayList<>();
        if (course.isCourseComplete(courseRecord)) {
            patches.add(PatchOp.replacePatch("state", State.COMPLETED.name()));
        } else if (courseRecord.getStateSafe().equals(State.NULL) ||
                (courseRecord.getStateSafe().equals(State.ARCHIVED))) {
            patches.add(PatchOp.replacePatch("state", State.IN_PROGRESS.name()));
        }
        return patches;
    }

    @Override
    public String getName() {
        return "Complete booking";
    }

    @Override
    public ModuleRecordStatus getCreateModuleRecordStatus() {
        throw new IncorrectStateException("Can't create a new module record when completing an event.");
    }

    @Override
    public List<PatchOp> getUpdateModuleRecordPatches(ModuleRecord moduleRecord) {
        List<PatchOp> patches = new ArrayList<>();
        if (moduleRecord.getStateSafe().equals(State.APPROVED)) {
            patches.add(PatchOp.replacePatch("state", State.COMPLETED.name()));
            patches.add(PatchOp.replacePatch("/completionDate", LocalDateTime.now(clock).toString()));
        } else {
            throw new IncorrectStateException("Can't complete a booking that hasn't been approved");
        }
        return patches;
    }
}
