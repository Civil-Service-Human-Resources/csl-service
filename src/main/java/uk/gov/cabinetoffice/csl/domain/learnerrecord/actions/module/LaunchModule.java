package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.IModuleAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

public class LaunchModule implements IModuleAction {

    @Override
    public ModuleRecord applyUpdates(ModuleRecord moduleRecord) {
        if (!moduleRecord.getState().equals(State.COMPLETED)) {
            moduleRecord.setState(State.IN_PROGRESS);
        }
        return moduleRecord;
    }

    @Override
    public ModuleRecordAction getAction() {
        return ModuleRecordAction.LAUNCH_MODULE;
    }

    @Override
    public boolean canCreateRecord() {
        return true;
    }

}
