package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.IModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.util.UtilService;

@Slf4j
public abstract class ModuleRecordActionProcessor implements IModuleRecordAction {

    protected final UtilService utilService;
    protected final Course course;
    protected final Module module;
    protected final User user;
    protected final ICourseRecordActionType actionType;

    protected ModuleRecordActionProcessor(UtilService utilService, CourseWithModule courseWithModule, User user,
                                          ICourseRecordActionType actionType) {
        this.utilService = utilService;
        this.course = courseWithModule.getCourse();
        this.module = courseWithModule.getModule();
        this.user = user;
        this.actionType = actionType;
    }

    @Override
    public String getCourseId() {
        return course.getId();
    }

    @Override
    public String getUserId() {
        return user.getId();
    }

    public String getModuleId() {
        return module.getId();
    }

    @Override
    public String getAction() {
        return actionType.getDescription();
    }

    @Override
    public String toString() {
        return String.format("Action: '%s' | Learner ID: %s | Course ID: %s | Module ID: %s", getAction(), user.getId(), course.getId(), module.getId());
    }

    @Override
    public ModuleRecord generateNewModuleRecord() {
        return applyUpdatesToModuleRecord(createModuleRecord());
    }

    protected ModuleRecord createModuleRecord() {
        return new ModuleRecord(getCourseId(), getUserId(), module.getId(), module.getTitle(), module.getModuleType(),
                module.getDuration(), module.isOptional(), module.getCost(), true);
    }
}
