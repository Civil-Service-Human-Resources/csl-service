package uk.gov.cabinetoffice.csl.config.notifications;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import uk.gov.cabinetoffice.csl.service.notification.messages.EmailNotification;

import java.util.Map;

import static uk.gov.cabinetoffice.csl.service.notification.messages.EmailNotification.NOTIFY_LINE_MANAGER_COMPLETED_LEARNING;

@Setter
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "notification-service")
public class EmailNotificationConfig {

    private Emails emails;

    @Bean("emailNameMap")
    public Map<EmailNotification, String> getEmailNameMap() {
        return Map.of(NOTIFY_LINE_MANAGER_COMPLETED_LEARNING, emails.getNotifyLineManagerCompletedLearning());
    }

}
