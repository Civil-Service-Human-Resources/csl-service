package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event.EventModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.ModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class LearnerRecordUpdateProcessor {

    private final LearnerRecordService learnerRecordService;
    private final CourseRecordActionFactory courseRecordActionFactory;
    private final IMessagingClient messagingClient;

    public CourseRecord processCourseRecordAction(Course course, User user, CourseRecordAction actionType) {
        ICourseRecordAction action = courseRecordActionFactory.getCourseRecordAction(course, user, actionType);
        return processCourseRecordAction(action);
    }

    public CourseRecord processModuleRecordAction(CourseWithModule courseWithModule, User user, ModuleRecordAction actionType) {
        return processModuleRecordActions(courseWithModule, user, List.of(actionType));
    }

    public CourseRecord processModuleRecordActions(CourseWithModule courseWithModule, User user, List<ModuleRecordAction> actionTypes) {
        ICourseRecordAction action = courseRecordActionFactory.getMultipleModuleRecordActions(courseWithModule, user, actionTypes);
        return processCourseRecordAction(action);
    }

    public CourseRecord processEventModuleRecordAction(CourseWithModuleWithEvent courseWithModuleWithEvent, User user, EventModuleRecordAction actionType) {
        ICourseRecordAction action = courseRecordActionFactory.getEventModuleRecordAction(courseWithModuleWithEvent, user, actionType);
        return processCourseRecordAction(action);
    }

    public CourseRecord processCourseRecordAction(ICourseRecordAction action) {
        try {
            log.info(String.format("Applying update %s", action.toString()));
            CourseRecord courseRecord = learnerRecordService.getCourseRecord(action.getUserId(), action.getCourseId());
            log.debug(String.format("Fetched course record: %s", courseRecord));
            if (courseRecord == null) {
                courseRecord = learnerRecordService.createCourseRecord(action.generateNewCourseRecord());
            } else {
                CourseRecord updatedRecord = action.applyUpdatesToCourseRecord(courseRecord);
                updatedRecord = learnerRecordService.updateCourseRecord(updatedRecord);
                log.debug(String.format("Updating with course record %s", updatedRecord));
                courseRecord.update(updatedRecord);
            }
            log.debug(String.format("Updated course record %s ", courseRecord));
            messagingClient.sendMessages(action.getMessages());
            return courseRecord;
        } catch (Exception e) {
            learnerRecordService.bustCourseRecordCache(action.getUserId(), action.getCourseId());
            throw e;
        }
    }

}
