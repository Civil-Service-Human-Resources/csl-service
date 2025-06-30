package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.CourseWithRecord;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.CourseRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionResult;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionWithId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordData;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.learningResources.course.CourseWithRecordService;
import uk.gov.cabinetoffice.csl.service.messaging.MessageMetadataFactory;
import uk.gov.cabinetoffice.csl.service.notification.NotificationFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseActionService {

    private final LearnerRecordService learnerRecordService;
    private final CourseWithRecordService courseWithRecordService;
    private final LearnerRecordActionWorker learnerRecordActionWorker;
    private final MessageMetadataFactory messageFactory;
    private final NotificationFactory emailFactory;
    private final ResponseFactory responseFactory;

    public CourseActionService(LearnerRecordService learnerRecordService, CourseWithRecordService courseWithRecordService,
                               LearnerRecordActionWorker learnerRecordActionWorker, MessageMetadataFactory messageFactory,
                               NotificationFactory emailFactory, ResponseFactory responseFactory) {
        this.learnerRecordService = learnerRecordService;
        this.courseWithRecordService = courseWithRecordService;
        this.learnerRecordActionWorker = learnerRecordActionWorker;
        this.messageFactory = messageFactory;
        this.emailFactory = emailFactory;
        this.responseFactory = responseFactory;
    }

    public ActionResult completeCourse(Course course, User user, LocalDateTime completionDate) {
        CourseRecordResourceId recordResourceId = new CourseRecordResourceId(user.getId(), course.getResourceId());
        ActionWithId action = new ActionWithId(recordResourceId, CourseRecordAction.COMPLETE_COURSE);
        action.setTimestamp(completionDate);
        LearnerRecord courseRecord = learnerRecordService.getLearnerRecord(recordResourceId);
        ActionResult actionResult = new ActionResult();
        LearnerRecordData courseRecordData = learnerRecordActionWorker.processAction(courseRecord, action);
        if (courseRecordData.hasNewRecords()) {
            actionResult.getMessages().add(messageFactory.generateCourseCompletionMessage(completionDate, user, course));
            if (user.hasLineManager() && course.isMandatoryLearningForUser(user)) {
                actionResult.getEmails().add(emailFactory.getRequiredLearningCompleteMessage(user, course));
            }
        }
        actionResult.getLearnerRecordResults().getLearnerRecordUpdates().add(courseRecordData);
        return actionResult;
    }

    public CourseResponse performCourseAction(ActionWithId actionWithId) {
        CourseWithRecord course = courseWithRecordService.get(actionWithId.getResourceId());
        LearnerRecordData courseRecordData = learnerRecordActionWorker.processAction(course.getRecord(), actionWithId);
        boolean success = false;
        if (courseRecordData != null) {
            success = learnerRecordService.processLearnerRecordUpdates(List.of(courseRecordData)).containsId(actionWithId.getResourceId());
        }
        return responseFactory.buildCourseResponseFromAction(actionWithId, course, success);
    }
}
