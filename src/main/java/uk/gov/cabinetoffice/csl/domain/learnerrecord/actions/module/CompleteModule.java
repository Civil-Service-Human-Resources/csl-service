package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;

import java.time.LocalDateTime;

@Slf4j
public class CompleteModule extends CompletingModuleAction {

    public CompleteModule(LocalDateTime completionDate) {
        super(completionDate);
    }

    public ModuleRecord applyUpdates(ModuleRecord moduleRecord) {
        moduleRecord.setState(State.COMPLETED);
        moduleRecord.setCompletionDate(completionDate);
        return moduleRecord;
    }

    @Override
    public ICourseRecordActionType getAction() {
        return ModuleRecordAction.COMPLETE_MODULE;
    }

    @Override
    public boolean canCreateRecord() {
        return true;
    }

}
