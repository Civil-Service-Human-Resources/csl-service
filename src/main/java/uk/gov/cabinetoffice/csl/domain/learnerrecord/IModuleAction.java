package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;

public interface IModuleAction {

    ICourseRecordActionType getAction();

    boolean canCreateRecord();

    ModuleRecord applyUpdates(ModuleRecord moduleRecord);

}
