package uk.gov.cabinetoffice.csl.config.notifications;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import uk.gov.cabinetoffice.csl.service.notification.messages.EmailNotification;

import java.util.Map;

@Setter
@Slf4j
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "notification-service")
public class EmailTemplates {

    private Map<String, String> emails;

    public String getTemplate(EmailNotification template) {
        String templateValue = this.emails.get(template.getConfigName());
        if (templateValue == null) {
            log.warn(String.format("Email template %s has not been mapped", template));
        }
        return templateValue;
    }

}
