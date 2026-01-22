package uk.gov.cabinetoffice.csl.service.notification;

import uk.gov.cabinetoffice.csl.service.notification.messages.IEmail;

import java.util.Collection;

public interface INotificationService {

    void sendEmail(IEmail emails);

    void sendEmails(Collection<IEmail> emails);

}
