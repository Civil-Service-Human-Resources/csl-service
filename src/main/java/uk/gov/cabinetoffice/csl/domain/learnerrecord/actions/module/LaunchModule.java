package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.util.UtilService;

public class LaunchModule extends ModuleRecordActionProcessor {

    public LaunchModule(UtilService utilService, CourseWithModule courseWithModule, User user) {
        super(utilService, courseWithModule, user, ModuleRecordAction.LAUNCH_MODULE);
    }

    @Override
    public ModuleRecord applyUpdatesToModuleRecord(ModuleRecord moduleRecord) {
        moduleRecord.setState(State.IN_PROGRESS);
        return moduleRecord;
    }

}
