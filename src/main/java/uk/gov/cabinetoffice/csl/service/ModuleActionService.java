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
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

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
    private final UserDetailsService userDetailsService;

    public ModuleActionService(ModuleRecordActionFactory moduleRecordActionFactory, ModuleRecordFactory moduleRecordFactory,
                               CourseActionService courseActionService, LearnerRecordService learnerRecordService,
                               ActionResultService actionResultService, UserDetailsService userDetailsService) {
        this.moduleRecordActionFactory = moduleRecordActionFactory;
        this.moduleRecordFactory = moduleRecordFactory;
        this.courseActionService = courseActionService;
        this.learnerRecordService = learnerRecordService;
        this.actionResultService = actionResultService;
        this.userDetailsService = userDetailsService;
    }

    private ModuleRecord processAction(@Nullable ModuleRecord moduleRecord, CourseWithModule courseWithModule, UserToModuleAction action) {
        if (moduleRecord == null) {
            if (action.getAction().canCreateRecord()) {
                moduleRecord = moduleRecordFactory.create(action.getUserId(), courseWithModule);
            } else {
                log.info("Module record was null and action {} cannot create new records. Skipping.", action.getAction());
                return null;
            }
        }
        moduleRecord = action.getAction().applyUpdates(moduleRecord);
        return moduleRecord;
    }

    public ModuleRecord launchModule(CourseWithModule courseWithModule, String userId) {
        UserToModuleAction action = new UserToModuleAction(userId, moduleRecordActionFactory.getLaunchModuleAction());
        return processModuleAction(courseWithModule, action);
    }

    public ModuleRecord processModuleAction(CourseWithModule courseWithModule, UserToModuleAction userAction) {
        return processModuleActions(courseWithModule, List.of(userAction)).get(new ModuleRecordResourceId(userAction.getUserId(), courseWithModule.getModule().getResourceId()).getAsString());
    }

    public Map<String, ModuleRecord> processModuleActions(CourseWithModule courseWithModule, List<UserToModuleAction> userActions) {
        Module module = courseWithModule.getModule();
        List<ModuleRecordResourceId> moduleRecordIds = userActions.stream().map(a -> new ModuleRecordResourceId(a.getUserId(), module.getId())).toList();
        Map<String, ModuleRecord> moduleRecordMap = learnerRecordService.getModuleRecords(moduleRecordIds)
                .stream().collect(Collectors.toMap(ModuleRecord::getLearnerRecordIdAsString, mr -> mr));
        return processModuleActions(courseWithModule, userActions, moduleRecordMap);
    }

    public Map<String, ModuleRecord> processModuleActions(CourseWithModule courseWithModule, List<UserToModuleAction> userActions, Map<String, ModuleRecord> moduleRecordMap) {
        LearnerRecordResults result = new LearnerRecordResults();
        for (UserToModuleAction userAction : userActions) {
            ModuleRecord moduleRecord = moduleRecordMap.get(String.format("%s,%s", userAction.getUserId(), courseWithModule.getModule().getResourceId()));
            moduleRecord = processAction(moduleRecord, courseWithModule, userAction);
            if (moduleRecord != null) {
                result.getModuleRecordUpdates().add(moduleRecord);
            }
        }
        return learnerRecordService.applyModuleRecordUpdates(result);
    }

    public void rollUpModule(CourseWithModule courseWithModule, CSLRusticiProps properties) {
        IModuleAction action = moduleRecordActionFactory.getRollUpModuleAction(properties);
        if (properties.getCompletionDate() != null) {
            completeModule(courseWithModule, properties.getLearnerId(), action);
        } else {
            processModuleAction(courseWithModule, new UserToModuleAction(properties.getLearnerId(), action));
        }
    }

    public void completeModule(CourseWithModule courseWithModule, String userId) {
        IModuleAction action = moduleRecordActionFactory.getCompleteModuleAction();
        completeModule(courseWithModule, userId, action);
    }

    public void completeModule(CourseWithModule courseWithModule, String userId, IModuleAction completionAction) {
        User user = userDetailsService.getUserWithUid(userId);
        Course course = courseWithModule.getCourse();
        ModuleRecordResourceId recordResourceId = new ModuleRecordResourceId(userId, courseWithModule.getModule().getResourceId());
        List<ModuleRecordResourceId> idsToFetch = new ArrayList<>(List.of(recordResourceId));
        List<ModuleRecordResourceId> requiredModuleIds = course.getRequiredModulesForCompletion()
                .stream().map(m -> new ModuleRecordResourceId(userId, m.getResourceId())).toList();
        boolean completeCourse = false;
        Map<String, ModuleRecord> moduleMap;
        if (requiredModuleIds.contains(recordResourceId)) {
            idsToFetch.addAll(requiredModuleIds.stream().filter(mr -> !mr.equals(recordResourceId)).toList());
            moduleMap = learnerRecordService.getModuleRecordsMap(idsToFetch);
            log.debug("Checking for course completion");
            LearningPeriod learningPeriod = course.getLearningPeriodForDepartmentHierarchy(user.getDepartmentCodes()).orElse(null);
            List<String> remainingModuleIds = new ArrayList<>();
            for (ModuleRecordResourceId requiredModuleId : requiredModuleIds) {
                log.info("Checking module with ID: {}", requiredModuleId.getResourceId());
                ModuleRecord mr = moduleMap.get(requiredModuleId.getAsString());
                State moduleRecordState = mr == null ? State.NULL : mr.getStateForLearningPeriod(learningPeriod);
                log.debug("Module {} state is {}", requiredModuleId, moduleRecordState);
                if (!moduleRecordState.equals(State.COMPLETED)) {
                    remainingModuleIds.add(requiredModuleId.getResourceId());
                    log.info("Module {} added to remaining modules", requiredModuleId.getResourceId());
                }
            }
            if (remainingModuleIds.size() == 1 && Objects.equals(remainingModuleIds.get(0), courseWithModule.getModule().getResourceId())) {
                log.info("Course set as complete");
                completeCourse = true;
            }
            else{
                log.info("Course could not be set as complete: ");
                log.info("Remaining module IDs: ", remainingModuleIds);
                log.info("Module ID from course with module: ", courseWithModule.getModule().getResourceId());
            }
        } else {
            moduleMap = learnerRecordService.getModuleRecordsMap(idsToFetch);
        }
        ModuleRecord moduleRecord = processModuleActions(courseWithModule, List.of(new UserToModuleAction(userId, completionAction)), moduleMap).get(recordResourceId.getAsString());
        if (completeCourse) {
            log.info("Completed module was the last required module remaining for course completion. Completing course.");
            ActionResult actionResult = courseActionService.completeCourse(course, user, moduleRecord.getCompletionDate());
            log.info("Completed course");
            actionResultService.processResults(actionResult);
            log.info("Results processed");
        }
    }
}
