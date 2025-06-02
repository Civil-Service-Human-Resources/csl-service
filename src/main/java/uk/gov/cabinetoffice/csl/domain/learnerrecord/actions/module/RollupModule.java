package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.IModuleAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.rustici.CSLRusticiProps;

public class RollupModule implements IModuleAction {

    private final CSLRusticiProps props;

    public RollupModule(CSLRusticiProps props) {
        this.props = props;
    }

    @Override
    public boolean canCreateRecord() {
        return false;
    }

    @Override
    public ModuleRecord applyUpdates(ModuleRecord moduleRecord) {
        if (props.getResult() != null) {
            moduleRecord.setResult(props.getResult());
        }
        if (props.getCompletionDate() != null) {
            moduleRecord.setState(State.COMPLETED);
            moduleRecord.setCompletionDate(props.getCompletionDate());
        }
        return moduleRecord;
    }

    @Override
    public ModuleRecordAction getAction() {
        return ModuleRecordAction.ROLLUP_MODULE;
    }
}
