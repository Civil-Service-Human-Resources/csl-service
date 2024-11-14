package uk.gov.cabinetoffice.csl.service.notification;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.notificationService.INotificationServiceClient;
import uk.gov.cabinetoffice.csl.domain.notificationservice.MessageDto;
import uk.gov.cabinetoffice.csl.service.notification.messages.EmailNotification;
import uk.gov.cabinetoffice.csl.service.notification.messages.IEmail;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.util.Collection;
import java.util.Map;

@Component
public class NotificationService implements INotificationService {


    private final INotificationServiceClient notificationServiceClient;
    private final Map<EmailNotification, String> emailNameMap;
    private final UtilService utilService;

    public NotificationService(INotificationServiceClient notificationServiceClient, Map<EmailNotification, String> emailNameMap, UtilService utilService) {
        this.notificationServiceClient = notificationServiceClient;
        this.emailNameMap = emailNameMap;
        this.utilService = utilService;
    }

    public void sendEmails(Collection<IEmail> emails) {
        emails.forEach(email -> {
            String uid = utilService.generateUUID();
            String name = emailNameMap.get(email.getEmailNotification());
            MessageDto messageDto = new MessageDto(uid, email.getRecipient(), email.getPersonalisation());
            notificationServiceClient.sendEmail(name, messageDto);
        });
    }
}
