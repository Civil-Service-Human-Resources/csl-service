package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionResult;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.notification.INotificationService;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Service
public class ActionResultService {

    private final LearnerRecordService learnerRecordService;
    private final IMessagingClient messagingClient;
    private final INotificationService notificationService;

    public ActionResultService(LearnerRecordService learnerRecordService, IMessagingClient messagingClient,
                               INotificationService notificationService) {
        this.learnerRecordService = learnerRecordService;
        this.messagingClient = messagingClient;
        this.notificationService = notificationService;
    }

    public void processResults(ActionResult actionResult) {
        if (!isEmpty(actionResult.getEmails())) {
            notificationService.sendEmails(actionResult.getEmails());
        }
        if (!isEmpty(actionResult.getMessages())) {
            messagingClient.sendMessages(actionResult.getMessages());
        }
        if (!isEmpty(actionResult.getLearnerRecordResults())) {
            try {
                learnerRecordService.applyModuleRecordUpdates(actionResult.getLearnerRecordResults());
                learnerRecordService.processLearnerRecordUpdates(actionResult.getLearnerRecordResults());
            } catch (Exception e) {
                learnerRecordService.bustModuleRecordCache(actionResult.getLearnerRecordResults().getModuleRecordIds());
                learnerRecordService.bustLearnerRecordCache(actionResult.getLearnerRecordResults().getLearnerRecordIds());
            }
        }
    }
}
