package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordUpdateProcessor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.ModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionResult;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordData;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.service.messaging.MessageMetadataFactory;
import uk.gov.cabinetoffice.csl.service.notification.NotificationFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ModuleActionService {

    private final MessageMetadataFactory messageFactory;
    private final NotificationFactory emailFactory;
    private final LearnerRecordService learnerRecordService;
    private final LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;

    public ModuleActionService(MessageMetadataFactory messageFactory, NotificationFactory emailFactory,
                               LearnerRecordService learnerRecordService, LearnerRecordUpdateProcessor learnerRecordUpdateProcessor) {
        this.messageFactory = messageFactory;
        this.emailFactory = emailFactory;
        this.learnerRecordService = learnerRecordService;
        this.learnerRecordUpdateProcessor = learnerRecordUpdateProcessor;
    }

    public void launchModule(CourseWithModule courseWithModule, User user) {
        LearnerRecordResourceId courseRecordId = new CourseRecordResourceId(user.getId(), courseWithModule.getCourse().getResourceId());
        learnerRecordService.getOrCreateLearnerRecord(courseRecordId);
        learnerRecordUpdateProcessor.processModuleRecordAction(courseWithModule, user, ModuleRecordAction.LAUNCH_MODULE);
    }

    public void completeModule(CourseWithModule courseWithModule, User user) {
        ActionResult actionResult = new ActionResult();
        Course course = courseWithModule.getCourse();
        List<ModuleRecordResourceId> requiredModuleIds = course.getRequiredModulesForCompletion()
                .stream().map(m -> new ModuleRecordResourceId(user.getId(), m.getResourceId())).toList();
        Map<String, ModuleRecord> moduleRecordMap = learnerRecordService.getModuleRecordsMap(requiredModuleIds);
        LearnerRecordData courseRecord = learnerRecordService.getLearnerRecordAsData(new CourseRecordResourceId(user.getId(), course.getResourceId()));
        ModuleRecord moduleRecord = learnerRecordUpdateProcessor.processModuleRecordAction(courseWithModule, user, ModuleRecordAction.COMPLETE_MODULE);
        if (moduleRecordMap.containsKey(moduleRecord.getLearnerRecordId().getAsString())) {
            List<String> remainingModules = new ArrayList<>(course.getRemainingModuleIdsForCompletion(moduleRecordMap, user));
            if (remainingModules.size() == 1 && Objects.equals(remainingModules.get(0), courseWithModule.getModule().getResourceId())) {
                courseRecord.addNewEvent(CourseRecordAction.COMPLETE_COURSE);
                actionResult.getMessages().add(messageFactory.generateCourseCompletionMessage(moduleRecord.getCompletionDate(), user, course));
                actionResult.getEmails().add(emailFactory.getRequiredLearningCompleteMessage(user, course));
            }
        }
        actionResult.getLearnerRecordResults().getLearnerRecordUpdates().add(courseRecord);

    }

}
