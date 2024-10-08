package uk.gov.cabinetoffice.csl.service.notification.messages;

import lombok.Getter;

import java.util.Map;

@Getter
public class NotifyLineManagerCompletedLearning implements IEmail {

    private final String lineManagerEmail;
    private final String lineManagerName;
    private final String learnerName;
    private final String learnerEmail;
    private final String courseTitle;

    public NotifyLineManagerCompletedLearning(String lineManagerEmail, String lineManagerName, String learnerName,
                                              String learnerEmail, String courseTitle) {
        this.lineManagerEmail = lineManagerEmail;
        this.lineManagerName = lineManagerName;
        this.learnerName = learnerName;
        this.learnerEmail = learnerEmail;
        this.courseTitle = courseTitle;
    }

    @Override
    public Map<String, String> getPersonalisation() {
        return Map.of("lineManagerName", lineManagerName,
                "learnerName", learnerName,
                "learnerEmail", learnerEmail,
                "courseTitle", courseTitle);
    }

    @Override
    public EmailNotification getEmailNotification() {
        return EmailNotification.NOTIFY_LINE_MANAGER_COMPLETED_LEARNING;
    }

    @Override
    public String getRecipient() {
        return lineManagerEmail;
    }

}
