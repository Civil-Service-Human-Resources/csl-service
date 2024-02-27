package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

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
        courseRecord = this.updateCourseRecord(courseRecord);
        courseRecord.setModuleRecords(courseRecord.getModuleRecords().stream().filter(mr -> Objects.equals(mr.getModuleId(), getModuleId())).collect(Collectors.toSet()));
        return courseRecord;
    }

    protected abstract CourseRecord updateCourseRecord(CourseRecord courseRecord);

    protected String getModuleId() {
        return module.getId();
    }

    protected boolean willModuleCompletionCompleteCourse(CourseRecord courseRecord) {
        List<Module> remainingModules = new ArrayList<>(course.getRemainingModulesForCompletion(courseRecord, user));
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
