package uk.gov.cabinetoffice.csl.service.notification.messages;

import lombok.Getter;

import java.util.Map;

@Getter
public class InviteLearnerToEventMessageParams implements IEmail {

    private final String recipient;
    private final String courseTitle;
    private final String courseDate;
    private final String courseLocation;
    private final String inviteLink;

    public InviteLearnerToEventMessageParams(String recipient, String courseTitle, String courseDate, String courseLocation, String inviteLink) {
        this.recipient = recipient;
        this.courseTitle = courseTitle;
        this.courseDate = courseDate;
        this.courseLocation = courseLocation;
        this.inviteLink = inviteLink;
    }

    @Override
    public EmailNotification getEmailNotification() {
        return EmailNotification.INVITE_LEARNER;
    }

    @Override
    public Map<String, String> getPersonalisation() {
        return Map.of(
                "learnerName", recipient,
                "courseTitle", courseTitle,
                "courseDate", courseDate,
                "courseLocation", courseLocation,
                "inviteLink", inviteLink
        );
    }
}
