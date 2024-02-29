package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordActionProcessor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.service.messaging.model.CourseCompletionMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public abstract class ModuleRecordActionProcessor extends CourseRecordActionProcessor {

    protected final Module module;
    protected final ICourseRecordActionType actionType;

    protected ModuleRecordActionProcessor(CourseWithModule courseWithModule, User user, ICourseRecordActionType actionType) {
        super(courseWithModule.getCourse(), user, actionType);
        this.module = courseWithModule.getModule();
        this.actionType = actionType;
    }

    @Override
    public CourseRecord applyUpdatesToCourseRecord(CourseRecord courseRecord) {
        CourseRecord updatedRecord = this.updateCourseRecord(courseRecord);
        updatedRecord.setModuleRecords(courseRecord.getModuleRecords().stream().filter(mr -> Objects.equals(mr.getModuleId(), getModuleId())).collect(Collectors.toSet()));
        return updatedRecord;
    }

    protected abstract CourseRecord updateCourseRecord(CourseRecord courseRecord);

    protected String getModuleId() {
        return module.getId();
    }

    protected boolean willModuleCompletionCompleteCourse(CourseRecord courseRecord) {
        log.debug(String.format("Checking if %s completion will complete course", module.getId()));
        List<Module> remainingModules = new ArrayList<>(course.getRemainingModulesForCompletion(courseRecord, user));
        log.debug(String.format("Remaining modules left for completion: %s", remainingModules.stream().map(Module::getId)));
        return (remainingModules.size() == 1 && Objects.equals(remainingModules.get(0).getId(), getModuleId()));
    }

    protected CourseCompletionMessage generateCompletionMessage() {
        return new CourseCompletionMessage(user.getId(), user.getEmail(), course.getId(), course.getTitle(),
                user.getOrganisationId(), user.getProfessionId(), user.getGradeId());
    }

    @Override
    public String toString() {
        return String.format("%s | Module ID: %s", super.toString(), module.getId());
    }
}
