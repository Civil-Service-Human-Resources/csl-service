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

    public CourseRecord processCourseRecordAction(Course course, User user, CourseRecordAction actionType) {
        ICourseRecordAction action = courseRecordActionFactory.getCourseRecordAction(course, user, actionType);
        return processCourseRecordAction(action);
    }

    public CourseRecord processModuleRecordAction(CourseWithModule courseWithModule, User user, ModuleRecordAction actionType, LocalDateTime completionDate) {
        return processModuleRecordActions(courseWithModule, user, List.of(actionType), completionDate);
    }

    public CourseRecord processModuleRecordActions(CourseWithModule courseWithModule, User user, List<ModuleRecordAction> actionTypes, LocalDateTime completionDate) {
        ICourseRecordAction action = courseRecordActionFactory.getMultipleModuleRecordActions(courseWithModule, user, actionTypes, completionDate);
        return processCourseRecordAction(action);
    }

    public CourseRecord processEventModuleRecordAction(CourseWithModuleWithEvent courseWithModuleWithEvent, User user, EventModuleRecordAction actionType) {
        ICourseRecordAction action = courseRecordActionFactory.getEventModuleRecordAction(courseWithModuleWithEvent, user, actionType);
        return processCourseRecordAction(action);
    }

    public Map<String, CourseRecord> processMultipleEventModuleRecordActions(CourseWithModuleWithEvent courseWithModuleWithEvent, List<UserToAction<EventModuleRecordAction>> users) {
        CourseRecordActionCollection actions = courseRecordActionFactory.getEventModuleRecordActions(courseWithModuleWithEvent, users);
        return processCourseRecordActions(actions);
    }

    public Map<String, CourseRecord> processCourseRecordActions(CourseRecordActionCollection actions) {
        List<CourseRecordId> courseRecordIds = actions.getCourseRecordIds();
        try {
            Map<String, CourseRecord> courseRecordMap = learnerRecordService.getCourseRecords(courseRecordIds)
                    .stream().collect(Collectors.toMap(CourseRecord::getId, cr -> cr));
            CourseRecordActionCollectionResult result = actions.process(courseRecordMap);
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

    public CourseRecord processCourseRecordAction(ICourseRecordAction action) {
        CourseRecordId courseRecordId = action.getCourseRecordId();
        CourseRecordActionCollection courseRecordActionCollection = CourseRecordActionCollection.createWithSingleAction(action);
        return processCourseRecordActions(courseRecordActionCollection).get(courseRecordId.getAsString());
    }

}
