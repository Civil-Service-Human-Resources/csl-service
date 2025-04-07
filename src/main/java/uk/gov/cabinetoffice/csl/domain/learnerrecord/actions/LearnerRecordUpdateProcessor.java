package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event.EventModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.ModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.notification.INotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class LearnerRecordUpdateProcessor {

    private final LearnerRecordService learnerRecordService;
    private final CourseRecordActionFactory courseRecordActionFactory;
    private final IMessagingClient messagingClient;
    private final INotificationService notificationService;

    public CourseRecord processCourseRecordAction(Course course, User user, CourseRecordAction actionType, LocalDateTime completedDate) {
        ICourseRecordAction action = courseRecordActionFactory.getCourseRecordAction(course, user, actionType);
        return processCourseRecordAction(action, completedDate);
    }

    public CourseRecord processModuleRecordAction(CourseWithModule courseWithModule, User user, ModuleRecordAction actionType, LocalDateTime completedDate) {
        return processModuleRecordActions(courseWithModule, user, List.of(actionType), completedDate);
    }

    public CourseRecord processModuleRecordActions(CourseWithModule courseWithModule, User user, List<ModuleRecordAction> actionTypes, LocalDateTime completedDate) {
        ICourseRecordAction action = courseRecordActionFactory.getMultipleModuleRecordActions(courseWithModule, user, actionTypes);
        return processCourseRecordAction(action, completedDate);
    }

    public CourseRecord processEventModuleRecordAction(CourseWithModuleWithEvent courseWithModuleWithEvent, User user, EventModuleRecordAction actionType, LocalDateTime completedDate) {
        ICourseRecordAction action = courseRecordActionFactory.getEventModuleRecordAction(courseWithModuleWithEvent, user, actionType);
        return processCourseRecordAction(action, completedDate);
    }

    public Map<String, CourseRecord> processMultipleEventModuleRecordActions(CourseWithModuleWithEvent courseWithModuleWithEvent, List<UserToAction<EventModuleRecordAction>> users, LocalDateTime completedDate) {
        CourseRecordActionCollection actions = courseRecordActionFactory.getEventModuleRecordActions(courseWithModuleWithEvent, users);
        return processCourseRecordActions(actions, completedDate);
    }

    public Map<String, CourseRecord> processCourseRecordActions(CourseRecordActionCollection actions, LocalDateTime completedDate) {
        List<CourseRecordId> courseRecordIds = actions.getCourseRecordIds();
        try {
            Map<String, CourseRecord> courseRecordMap = learnerRecordService.getCourseRecords(courseRecordIds)
                    .stream().collect(Collectors.toMap(CourseRecord::getId, cr -> cr));
            CourseRecordActionCollectionResult result = actions.process(courseRecordMap, completedDate);
            if (!result.getNewRecords().isEmpty()) {
                learnerRecordService.createCourseRecords(result.getNewRecords()).forEach(cr -> courseRecordMap.put(cr.getId(), cr));
            }
            if (!result.getUpdatedRecords().isEmpty()) {
                courseRecordMap.putAll(learnerRecordService.updateCourseRecords(result.getUpdatedRecords()));
            }
            if (!result.getMessages().isEmpty()) {
                messagingClient.sendMessages(result.getMessages());
            }
            if (!result.getEmails().isEmpty()) {
                notificationService.sendEmails(result.getEmails());
            }
            return courseRecordMap;
        } catch (Exception e) {
            learnerRecordService.bustCourseRecordCache(courseRecordIds);
            throw e;
        }
    }

    public CourseRecord processCourseRecordAction(ICourseRecordAction action,LocalDateTime completedDate) {
        CourseRecordId courseRecordId = action.getCourseRecordId();
        CourseRecordActionCollection courseRecordActionCollection = CourseRecordActionCollection.createWithSingleAction(action);
        return processCourseRecordActions(courseRecordActionCollection, completedDate).get(courseRecordId.getAsString());
    }

}
