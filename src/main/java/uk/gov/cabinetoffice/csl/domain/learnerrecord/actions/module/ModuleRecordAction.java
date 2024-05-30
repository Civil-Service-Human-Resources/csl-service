package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;

public enum ModuleRecordAction implements ICourseRecordActionType {
    LAUNCH_MODULE("Launch a module"),
    COMPLETE_MODULE("Complete a module"),
    ROLLUP_COMPLETE_MODULE("Complete a module with rollup data"),
    PASS_MODULE("Pass a module"),
    FAIL_MODULE("Fail a module");

    private final String description;

    ModuleRecordAction(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
