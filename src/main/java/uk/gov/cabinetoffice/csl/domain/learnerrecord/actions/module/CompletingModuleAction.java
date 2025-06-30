package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import lombok.Getter;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.IModuleAction;

import java.time.LocalDateTime;

@Getter
public abstract class CompletingModuleAction implements IModuleAction {

    protected final LocalDateTime completionDate;

    public CompletingModuleAction(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

}
