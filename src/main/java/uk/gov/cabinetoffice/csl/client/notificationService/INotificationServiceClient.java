package uk.gov.cabinetoffice.csl.client.notificationService;

import uk.gov.cabinetoffice.csl.domain.notificationservice.MessageDto;

public interface INotificationServiceClient {

    void sendEmail(String emailName, MessageDto body);

}
