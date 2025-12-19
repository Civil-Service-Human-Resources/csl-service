package uk.gov.cabinetoffice.csl.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.notificationService.INotificationServiceClient;
import uk.gov.cabinetoffice.csl.config.notifications.EmailTemplates;
import uk.gov.cabinetoffice.csl.domain.notificationservice.MessageDto;
import uk.gov.cabinetoffice.csl.service.notification.messages.IEmail;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.util.Collection;

@Component
@Slf4j
public class NotificationService implements INotificationService {

    private final INotificationServiceClient notificationServiceClient;
    private final EmailTemplates emailNameMap;
    private final UtilService utilService;

    public NotificationService(INotificationServiceClient notificationServiceClient, EmailTemplates emailNameMap, UtilService utilService) {
        this.notificationServiceClient = notificationServiceClient;
        this.emailNameMap = emailNameMap;
        this.utilService = utilService;
    }

    @Override
    public void sendEmail(IEmail email) {
        String uid = utilService.generateUUID();
        String name = emailNameMap.getTemplate(email.getEmailNotification());
        MessageDto messageDto = new MessageDto(uid, email.getRecipient(), email.getPersonalisation());
        notificationServiceClient.sendEmail(name, messageDto);
    }

    public void sendEmails(Collection<IEmail> emails) {
        emails.forEach(this::sendEmail);
    }
}
