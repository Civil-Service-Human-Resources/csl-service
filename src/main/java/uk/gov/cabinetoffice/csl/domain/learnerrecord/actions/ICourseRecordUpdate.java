package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;

import java.util.List;

public interface ICourseRecordUpdate {

    CourseRecordStatus getCreateCourseRecordStatus();

    List<PatchOp> getUpdateCourseRecordPatches(CourseRecord courseRecord);

    String getName();
}
