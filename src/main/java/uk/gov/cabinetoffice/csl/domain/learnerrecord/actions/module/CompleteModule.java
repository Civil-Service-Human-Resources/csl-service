package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.service.notification.messages.NotifyLineManagerCompletedLearning;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class CompleteModule extends ModuleRecordActionProcessor {

    public CompleteModule(UtilService utilService, CourseWithModule courseWithModule, User user) {
        super(utilService, courseWithModule, user, ModuleRecordAction.COMPLETE_MODULE);
    }

    @Override
    public CourseRecord updateCourseRecord(CourseRecord courseRecord, LocalDateTime completedDate) {
        ModuleRecord moduleRecord = courseRecord.getOrCreateModuleRecord(module);
        LocalDateTime completionDate = utilService.getNowDateTime();
        List<String> remainingModules = new ArrayList<>(course.getRemainingModuleIdsForCompletion(courseRecord, user));
        if (remainingModules.size() == 1 && Objects.equals(remainingModules.get(0), getModuleId())) {
            log.debug(String.format("Completing module %s will complete this course. Setting course record to completed and sending completion message", getModuleId()));
            courseRecord.setState(State.COMPLETED);
            addMessage(generateCompletionMessage(completionDate));
            if (user.hasLineManager() && course.isMandatoryLearningForUser(user)) {
                addEmail(new NotifyLineManagerCompletedLearning(user.getLineManagerEmail(), user.getLineManagerName(),
                        user.getName(), user.getEmail(), course.getTitle()));
            }
        } else if (remainingModules.size() == 0 && !courseRecord.getState().equals(State.COMPLETED)) {
            log.debug("Course was already completed but course record status is not set to COMPLETED, setting status to COMPLETED.");
            courseRecord.setState(State.COMPLETED);
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
        return applyUpdatesToCourseRecord(createCourseRecord(), null);
    }
}
