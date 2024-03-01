package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.time.LocalDateTime;

@Slf4j
public class CompleteModule extends ModuleRecordActionProcessor {

    public CompleteModule(UtilService utilService, CourseWithModule courseWithModule, User user) {
        super(utilService, courseWithModule, user, ModuleRecordAction.COMPLETE_MODULE);
    }

    @Override
    public CourseRecord updateCourseRecord(CourseRecord courseRecord) {
        ModuleRecord moduleRecord = courseRecord.getOrCreateModuleRecord(module);
        LocalDateTime completionDate = utilService.getNowDateTime();
        if (willModuleCompletionCompleteCourse(courseRecord)) {
            log.debug(String.format("Completing module %s will complete this course. Setting course record to completed and sending completion message", getModuleId()));
            courseRecord.setState(State.COMPLETED);
            messages.add(generateCompletionMessage(completionDate));
        } else if (courseRecord.getState().equals(State.NULL) ||
                courseRecord.getState().equals(State.ARCHIVED)) {
            courseRecord.setState(State.IN_PROGRESS);
        }
        moduleRecord.setState(State.COMPLETED);
        moduleRecord.setCompletionDate(completionDate);
        return courseRecord;
    }

    @Override
    public CourseRecord generateNewCourseRecord() {
        return applyUpdatesToCourseRecord(createCourseRecord());
    }
}
