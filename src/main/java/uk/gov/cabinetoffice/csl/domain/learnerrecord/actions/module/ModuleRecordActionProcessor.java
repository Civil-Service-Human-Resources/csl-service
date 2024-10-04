package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordActionProcessor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.service.messaging.model.CourseCompletionMessage;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public abstract class ModuleRecordActionProcessor extends CourseRecordActionProcessor {

    protected final Module module;
    protected final ICourseRecordActionType actionType;

    protected ModuleRecordActionProcessor(UtilService utilService, CourseWithModule courseWithModule, User user,
                                          ICourseRecordActionType actionType) {
        super(utilService, courseWithModule.getCourse(), user, actionType);
        this.module = courseWithModule.getModule();
        this.actionType = actionType;
    }

    @Override
    public CourseRecord applyUpdatesToCourseRecord(CourseRecord courseRecord) {
        CourseRecord updatedRecord = new CourseRecord(courseRecord.getCourseId(), courseRecord.getUserId(), courseRecord.getCourseTitle());
        updatedRecord.update(this.updateCourseRecord(courseRecord));
        updatedRecord.setModuleRecords(courseRecord.getModuleRecords().stream().filter(mr -> Objects.equals(mr.getModuleId(), getModuleId())).collect(Collectors.toSet()));
        return updatedRecord;
    }

    protected abstract CourseRecord updateCourseRecord(CourseRecord courseRecord);

    protected String getModuleId() {
        return module.getId();
    }

    protected boolean willModuleCompletionCompleteCourse(CourseRecord courseRecord) {
        log.debug(String.format("Checking if %s completion will complete course", module.getId()));
        List<String> remainingModules = new ArrayList<>(course.getRemainingModuleIdsForCompletion(courseRecord, user));
        log.debug(String.format("Remaining modules left for completion: %s", remainingModules));
        return (remainingModules.size() == 1 && Objects.equals(remainingModules.get(0), getModuleId()));
    }

    protected CourseCompletionMessage generateCompletionMessage(LocalDateTime completionDate) {
        return new CourseCompletionMessage(completionDate, user.getId(), user.getEmail(), course.getId(), course.getTitle(),
                user.getOrganisationId(), user.getFormattedOrganisationName(), user.getProfessionId(), user.getProfessionName(), user.getGradeId(), user.getGradeName());
    }

    @Override
    public String toString() {
        return String.format("%s | Module ID: %s", super.toString(), module.getId());
    }
}
