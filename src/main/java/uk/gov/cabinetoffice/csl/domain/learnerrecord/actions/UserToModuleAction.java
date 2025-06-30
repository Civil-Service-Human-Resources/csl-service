package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.Getter;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.IModuleAction;

@Getter
public class UserToModuleAction {
    private final String userId;
    private final IModuleAction action;

    public UserToModuleAction(String userId, IModuleAction action) {
        this.userId = userId;
        this.action = action;
    }
}
