package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;

import java.util.List;

public interface ModuleRecordUpdate extends CourseRecordUpdate {
    ModuleRecordStatus getCreateModuleRecordStatus();

    List<PatchOp> getUpdateModuleRecordPatches(ModuleRecord moduleRecord);
}
