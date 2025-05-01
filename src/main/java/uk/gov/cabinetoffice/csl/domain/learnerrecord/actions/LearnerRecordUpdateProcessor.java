package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event.EventModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.ModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class LearnerRecordUpdateProcessor {

    private final LearnerRecordService learnerRecordService;
    private final CourseRecordActionFactory courseRecordActionFactory;

    public ModuleRecord processModuleRecordAction(CourseWithModule courseWithModule, User user, ModuleRecordAction actionType) {
        return processModuleRecordActions(courseWithModule, user, List.of(actionType)).get(String.format("%s,%s", user.getId(), courseWithModule.getModule().getId()));
    }

    public Map<String, ModuleRecord> processModuleRecordActions(CourseWithModule courseWithModule, User user, List<ModuleRecordAction> actionTypes) {
        IModuleRecordAction action = courseRecordActionFactory.getMultipleModuleRecordActions(courseWithModule, user, actionTypes);
        return processModuleRecordAction(action);
    }

    public void processEventModuleRecordAction(CourseWithModuleWithEvent courseWithModuleWithEvent, User user, EventModuleRecordAction actionType) {
        IModuleRecordAction action = courseRecordActionFactory.getEventModuleRecordAction(courseWithModuleWithEvent, user, actionType);
        processModuleRecordAction(action);
    }

    public void processMultipleEventModuleRecordActions(CourseWithModuleWithEvent courseWithModuleWithEvent, List<UserToAction<EventModuleRecordAction>> users) {
        ModuleRecordActionCollection actions = courseRecordActionFactory.getEventModuleRecordActions(courseWithModuleWithEvent, users);
        processModuleRecordActions(actions);
    }

    public Map<String, ModuleRecord> processModuleRecordActions(ModuleRecordActionCollection actions) {
        Map<String, ModuleRecord> map = new HashMap<>();
        List<ModuleRecordResourceId> moduleRecordIds = actions.getModuleRecordIds();
        try {
            Map<String, ModuleRecord> moduleRecordMap = learnerRecordService.getModuleRecords(actions.getModuleRecordIds())
                    .stream().collect(Collectors.toMap(mr -> mr.getLearnerRecordId().getAsString(), mr -> mr));
            ModuleRecordActionCollectionResult result = actions.process(moduleRecordMap);
            if (!result.getNewRecords().isEmpty()) {
                learnerRecordService.createModuleRecords(result.getNewRecords())
                        .forEach(mr -> map.put(mr.getLearnerRecordId().getAsString(), mr));
            }
            if (!result.getUpdatedRecords().isEmpty()) {
                learnerRecordService.updateModuleRecords(result.getUpdatedRecords())
                        .forEach(mr -> map.put(mr.getLearnerRecordId().getAsString(), mr));
            }
            return map;
        } catch (Exception e) {
            learnerRecordService.bustModuleRecordCache(moduleRecordIds);
            throw e;
        }
    }

    public Map<String, ModuleRecord> processModuleRecordAction(IModuleRecordAction action) {
        ModuleRecordActionCollection courseRecordActionCollection = ModuleRecordActionCollection.createWithSingleAction(action);
        return processModuleRecordActions(courseRecordActionCollection);
    }

}
