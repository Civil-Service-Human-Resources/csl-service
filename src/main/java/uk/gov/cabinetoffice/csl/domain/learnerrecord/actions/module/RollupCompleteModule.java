package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.error.RecordNotFoundException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.time.LocalDateTime;

public class RollupCompleteModule extends CompleteModule {

    public RollupCompleteModule(UtilService utilService, CourseWithModule courseWithModule, User user, LocalDateTime completionDate) {
        super(utilService, courseWithModule, user, completionDate);
    }

    @Override
    public ModuleRecord generateNewModuleRecord() {
        throw new RecordNotFoundException("Can't complete a module via rollup if the module record does not exist");
    }
}
