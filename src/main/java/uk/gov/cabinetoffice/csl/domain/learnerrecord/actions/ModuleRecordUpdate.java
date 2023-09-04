package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;

import java.util.List;

public interface ModuleRecordUpdate extends CourseRecordUpdate {
    CourseRecordStatus getCreateModuleRecordStatus();

    List<PatchOp> getUpdateModuleRecordPatches();
}
