package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Result;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.util.UtilService;

public class PassModule extends ModuleRecordActionProcessor {

    public PassModule(UtilService utilService, CourseWithModule courseWithModule, User user) {
        super(utilService, courseWithModule, user, ModuleRecordAction.PASS_MODULE);
    }

    @Override
    public ModuleRecord applyUpdatesToModuleRecord(ModuleRecord moduleRecord) {
        moduleRecord.setResult(Result.PASSED);
        return moduleRecord;
    }

    @Override
    public ModuleRecord generateNewModuleRecord() {
        throw new IncorrectStateException("Can't create a new module record when passing a module.");
    }
}
