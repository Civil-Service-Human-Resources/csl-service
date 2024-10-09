package uk.gov.cabinetoffice.csl.client.notificationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.notificationservice.MessageDto;

@Component
@Slf4j
public class NotificationServiceClient implements INotificationServiceClient {

    @Value("${notificationService.emailUrl}")
    private String emails;
    private final IHttpClient httpClient;

    public NotificationServiceClient(@Qualifier("notificationServiceHttpClient") IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public void sendEmail(String emailName, MessageDto body) {
        log.info(String.format("Sending email '%s'", emailName));
        String url = String.format("%s/%s/send", emails, emailName);
        RequestEntity<MessageDto> request = RequestEntity.post(url).body(body);
        httpClient.executeRequest(request, Void.class);
    }
}
