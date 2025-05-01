package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecordResourceId;

public interface IModuleRecordAction {

    ModuleRecord applyUpdatesToModuleRecord(ModuleRecord moduleRecord);

    ModuleRecord generateNewModuleRecord();

    String getCourseId();

    String getModuleId();

    String getUserId();

    default ModuleRecordResourceId getModuleRecordId() {
        return new ModuleRecordResourceId(getUserId(), getModuleId());
    }

    String getAction();
}
