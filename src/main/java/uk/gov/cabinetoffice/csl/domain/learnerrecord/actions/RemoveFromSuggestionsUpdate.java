package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Preference;

import java.util.List;

@Component
public class RemoveFromSuggestionsUpdate implements ICourseRecordUpdate {
    @Override
    public CourseRecordStatus getCreateCourseRecordStatus() {
        return CourseRecordStatus.builder().preference(Preference.DISLIKED.name()).build();
    }

    @Override
    public List<PatchOp> getUpdateCourseRecordPatches(CourseRecord courseRecord) {
        throw new IncorrectStateException("Can't remove a course from suggestions when there is a course record present");
    }

    @Override
    public String getName() {
        return "Remove from suggestions";
    }
}
