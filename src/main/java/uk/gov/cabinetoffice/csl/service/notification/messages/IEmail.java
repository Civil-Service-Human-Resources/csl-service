package uk.gov.cabinetoffice.csl.service.notification.messages;

import java.util.Map;

public interface IEmail {

    Map<String, String> getPersonalisation();

    EmailNotification getEmailNotification();

    String getRecipient();

}
