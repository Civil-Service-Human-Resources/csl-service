package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.IModuleAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ModuleRecordActionFactory;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.UserToModuleAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionResult;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordResults;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.rustici.CSLRusticiProps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ModuleActionService {
    private final ModuleRecordActionFactory moduleRecordActionFactory;
    private final ModuleRecordFactory moduleRecordFactory;
    private final CourseActionService courseActionService;
    private final LearnerRecordService learnerRecordService;
    private final ActionResultService actionResultService;

    public ModuleActionService(ModuleRecordActionFactory moduleRecordActionFactory, ModuleRecordFactory moduleRecordFactory,
                               CourseActionService courseActionService, LearnerRecordService learnerRecordService, ActionResultService actionResultService) {
        this.moduleRecordActionFactory = moduleRecordActionFactory;
        this.moduleRecordFactory = moduleRecordFactory;
        this.courseActionService = courseActionService;
        this.learnerRecordService = learnerRecordService;
        this.actionResultService = actionResultService;
    }

    private ModuleRecord processAction(@Nullable ModuleRecord moduleRecord, CourseWithModule courseWithModule, UserToModuleAction action) {
        if (moduleRecord == null && action.getAction().canCreateRecord()) {
            moduleRecord = moduleRecordFactory.create(action.getUserId(), courseWithModule);
        } else {
            log.info(String.format("Module record was null and action %s cannot create new records. Skipping.", action.getAction()));
        }
        if (moduleRecord != null) {
            moduleRecord = action.getAction().applyUpdates(moduleRecord);
        }
        return moduleRecord;
    }

    public ModuleRecord launchModule(CourseWithModule courseWithModule, User user) {
        UserToModuleAction action = new UserToModuleAction(user.getId(), moduleRecordActionFactory.getLaunchModuleAction());
        return processModuleAction(courseWithModule, action);
    }

    public ModuleRecord processModuleAction(CourseWithModule courseWithModule, UserToModuleAction userAction) {
        return processModuleActions(courseWithModule, List.of(userAction)).get(new ModuleRecordResourceId(userAction.getUserId(), courseWithModule.getModule().getResourceId()).getAsString());
    }

    public Map<String, ModuleRecord> processModuleActions(CourseWithModule courseWithModule, List<UserToModuleAction> userActions) {
        LearnerRecordResults result = new LearnerRecordResults();
        Module module = courseWithModule.getModule();
        List<ModuleRecordResourceId> moduleRecordIds = userActions.stream().map(a -> new ModuleRecordResourceId(a.getUserId(), module.getId())).toList();
        Map<String, ModuleRecord> moduleRecordMap = learnerRecordService.getModuleRecords(moduleRecordIds)
                .stream().collect(Collectors.toMap(ModuleRecord::getUserId, mr -> mr));
        for (UserToModuleAction userAction : userActions) {
            ModuleRecord moduleRecord = moduleRecordMap.get(userAction.getUserId());
            moduleRecord = processAction(moduleRecord, courseWithModule, userAction);
            if (moduleRecord != null) {
                result.getModuleRecordUpdates().add(moduleRecord);
            }
        }
        return learnerRecordService.applyModuleRecordUpdates(result);
    }

    public void rollUpModule(CourseWithModule courseWithModule, User user, CSLRusticiProps properties) {
        IModuleAction action = moduleRecordActionFactory.getRollUpModuleAction(properties);
        if (properties.getCompletionDate() != null) {
            completeModule(courseWithModule, user, action);
        } else {
            processModuleAction(courseWithModule, new UserToModuleAction(user.getId(), action));
        }
    }

    public void completeModule(CourseWithModule courseWithModule, User user) {
        IModuleAction action = moduleRecordActionFactory.getCompleteModuleAction();
        completeModule(courseWithModule, user, action);
    }

    public void completeModule(CourseWithModule courseWithModule, User user, IModuleAction completionAction) {
        Course course = courseWithModule.getCourse();
        ModuleRecordResourceId recordResourceId = new ModuleRecordResourceId(user.getId(), courseWithModule.getModule().getResourceId());
        List<ModuleRecordResourceId> idsToFetch = new ArrayList<>(List.of(recordResourceId));
        List<ModuleRecordResourceId> requiredModuleIds = course.getRequiredModulesForCompletion()
                .stream().map(m -> new ModuleRecordResourceId(user.getId(), m.getResourceId())).toList();
        boolean checkForCompleteCourse = false;
        if (requiredModuleIds.contains(recordResourceId)) {
            idsToFetch.addAll(requiredModuleIds.stream().filter(mr -> !mr.equals(recordResourceId)).toList());
            checkForCompleteCourse = true;
        }
        Map<String, ModuleRecord> moduleMap = learnerRecordService.getModuleRecordsMap(idsToFetch);
        ModuleRecord moduleRecord = moduleMap.get(recordResourceId.getAsString());
        ActionResult actionResult = new ActionResult();
        moduleRecord = processAction(moduleRecord, courseWithModule, new UserToModuleAction(user.getId(), completionAction));
        actionResult.getLearnerRecordResults().getModuleRecordUpdates().add(moduleRecord);
        if (checkForCompleteCourse) {
            LearningPeriod learningPeriod = course.getLearningPeriodForDepartmentHierarchy(user.getDepartmentCodes()).orElse(null);
            List<String> remainingModuleIds = new ArrayList<>();
            for (ModuleRecordResourceId requiredModuleId : requiredModuleIds) {
                ModuleRecord mr = moduleMap.get(requiredModuleId.getAsString());
                if (mr == null || !mr.getStateForLearningPeriod(learningPeriod).equals(State.COMPLETED)) {
                    remainingModuleIds.add(requiredModuleId.getResourceId());
                }
            }
            if (remainingModuleIds.size() == 1 && Objects.equals(remainingModuleIds.get(0), courseWithModule.getModule().getResourceId())) {
                actionResult.add(courseActionService.completeCourse(course, user, moduleRecord.getCompletionDate()));
            }
        }
        actionResultService.processResults(actionResult);
    }
}
