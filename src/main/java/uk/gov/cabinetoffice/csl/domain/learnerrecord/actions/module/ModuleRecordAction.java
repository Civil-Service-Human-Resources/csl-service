package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;

public enum ModuleRecordAction implements ICourseRecordActionType {
    LAUNCH_MODULE("Launch a module"),
    COMPLETE_MODULE("Complete a module"),
    ROLLUP_MODULE("Process rollup for a module");

    private final String description;

    ModuleRecordAction(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
