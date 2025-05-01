package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.time.LocalDateTime;

@Slf4j
public class CompleteModule extends ModuleRecordActionProcessor {

    private LocalDateTime completionDate;

    public CompleteModule(UtilService utilService, CourseWithModule courseWithModule, User user, LocalDateTime completionDate) {
        super(utilService, courseWithModule, user, ModuleRecordAction.COMPLETE_MODULE);
        this.completionDate = completionDate;
    }

    @Override
    public ModuleRecord applyUpdatesToModuleRecord(ModuleRecord moduleRecord) {
        LocalDateTime completionDate = utilService.getNowDateTime();
        moduleRecord.setState(State.COMPLETED);
        moduleRecord.setCompletionDate(completionDate);
        return moduleRecord;
    }

    @Override
    public ModuleRecord generateNewModuleRecord() {
        return applyUpdatesToModuleRecord(createModuleRecord());
    }
}
