package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.error.RecordNotFoundException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.util.UtilService;

public class RollupCompleteModule extends CompleteModule {

    public RollupCompleteModule(UtilService utilService, CourseWithModule courseWithModule, User user) {
        super(utilService, courseWithModule, user);
    }

    @Override
    public CourseRecord updateCourseRecord(CourseRecord courseRecord) {
        courseRecord.getModuleRecord(getModuleId())
                .orElseThrow(() -> new RecordNotFoundException("Can't complete a module via rollup if the module record does not exist"));
        return super.updateCourseRecord(courseRecord);
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        throw new RecordNotFoundException("Can't complete a module via rollup if the course record does not exist");
    }
}
