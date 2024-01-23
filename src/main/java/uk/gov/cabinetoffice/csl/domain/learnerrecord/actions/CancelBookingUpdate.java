package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingStatus;

import java.util.ArrayList;
import java.util.List;

public class CancelBookingUpdate implements IModuleRecordUpdate {

    @Override
    public CourseRecordStatus getCreateCourseRecordStatus() {
        throw new IncorrectStateException("Can't create a new course record when cancelling an event.");
    }

    @Override
    public List<PatchOp> getUpdateCourseRecordPatches(CourseRecord courseRecord) {
        List<PatchOp> patches = new ArrayList<>();
        if (courseRecord.getStateSafe().equals(State.NULL) ||
                !courseRecord.getStateSafe().equals(State.IN_PROGRESS)) {
            patches.add(PatchOp.replacePatch("state", State.UNREGISTERED.name()));
        }
        return patches;
    }

    @Override
    public String getName() {
        return "Book event";
    }

    @Override
    public ModuleRecordStatus getCreateModuleRecordStatus() {
        throw new IncorrectStateException("Can't create a new module record when cancelling an event.");
    }

    @Override
    public List<PatchOp> getUpdateModuleRecordPatches(ModuleRecord moduleRecord) {
        return List.of(
                PatchOp.replacePatch("state", State.UNREGISTERED.name()),
                PatchOp.replacePatch("bookingStatus", BookingStatus.CANCELLED.name()),
                PatchOp.removePatch("result"),
                PatchOp.removePatch("score"),
                PatchOp.removePatch("completionDate")
        );
    }
}
