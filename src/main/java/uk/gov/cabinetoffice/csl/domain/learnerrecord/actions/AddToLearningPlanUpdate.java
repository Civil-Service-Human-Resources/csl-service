package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Preference;

import java.util.List;

@Component
public class AddToLearningPlanUpdate implements ICourseRecordUpdate {

    @Override
    public CourseRecordStatus getCreateCourseRecordStatus() {
        return CourseRecordStatus.builder().preference(Preference.LIKED.name()).build();
    }

    @Override
    public List<PatchOp> getUpdateCourseRecordPatches(CourseRecord courseRecord) {
        return List.of(
                PatchOp.replacePatch("preference", Preference.LIKED.name()),
                PatchOp.removePatch("state")
        );
    }

    @Override
    public String getName() {
        return "Add to Learning plan";
    }
}
