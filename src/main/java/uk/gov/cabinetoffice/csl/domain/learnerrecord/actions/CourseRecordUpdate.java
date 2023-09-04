package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;

import java.util.List;

public interface CourseRecordUpdate {

    CourseRecordStatus getCreateCourseRecordStatus();

    List<PatchOp> getUpdateCourseRecordPatches();

    String getName();
}
