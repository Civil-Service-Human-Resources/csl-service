package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Result;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.time.LocalDateTime;

public class PassModule extends ModuleRecordActionProcessor {

    public PassModule(UtilService utilService, CourseWithModule courseWithModule, User user) {
        super(utilService, courseWithModule, user, ModuleRecordAction.PASS_MODULE);
    }

    @Override
    public CourseRecord updateCourseRecord(CourseRecord courseRecord, LocalDateTime completionDate) {
        ModuleRecord moduleRecord = courseRecord.getModuleRecord(getModuleId())
                .orElseThrow(() -> new IncorrectStateException("Can't create a new module record when passing a module."));
        moduleRecord.setResult(Result.PASSED);
        return courseRecord;
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        throw new IncorrectStateException("Can't create a new course record when passing a module.");
    }
}
