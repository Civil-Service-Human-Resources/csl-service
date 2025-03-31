package uk.gov.cabinetoffice.csl.service.notification.messages;

import lombok.Getter;

import java.util.Map;

@Getter
public class NotifyUserCancelledEvent implements IEmail {

    private final String recipient;
    private final String cancellationReason;
    private final String courseTitle;
    private final String courseDate;
    private final String courseLocation;
    private final String bookingReference;

    public NotifyUserCancelledEvent(String recipient, String cancellationReason, String courseTitle, String courseDate,
                                    String courseLocation, String bookingReference) {
        this.recipient = recipient;
        this.cancellationReason = cancellationReason;
        this.courseTitle = courseTitle;
        this.courseDate = courseDate;
        this.courseLocation = courseLocation;
        this.bookingReference = bookingReference;
    }

    @Override
    public Map<String, String> getPersonalisation() {
        return Map.of(
                "learnerName", recipient,
                "cancellationReason", cancellationReason,
                "courseTitle", courseTitle,
                "courseDate", courseDate,
                "courseLocation", courseLocation,
                "bookingReference", bookingReference
        );
    }

    @Override
    public EmailNotification getEmailNotification() {
        return EmailNotification.NOTIFY_LEARNER_CANCELLED_EVENT;
    }

    @Override
    public String getRecipient() {
        return recipient;
    }

}
