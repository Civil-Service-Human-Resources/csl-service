package uk.gov.cabinetoffice.csl.service.notification.messages;

import lombok.Getter;

import java.util.Map;

@Getter
public class RequestBookingLMMessageParams implements IEmail {

    private final String recipient;
    private final String learnerName;
    private final String learnerEmail;
    private final String courseTitle;
    private final String courseDate;
    private final String courseLocation;
    private final String costInPounds;
    private final String bookingReference;

    public RequestBookingLMMessageParams(String recipient, String learnerName, String learnerEmail, String courseTitle, String courseDate, String courseLocation, String costInPounds, String bookingReference) {
        this.recipient = recipient;
        this.learnerName = learnerName;
        this.learnerEmail = learnerEmail;
        this.courseTitle = courseTitle;
        this.courseDate = courseDate;
        this.courseLocation = courseLocation;
        this.costInPounds = costInPounds;
        this.bookingReference = bookingReference;
    }

    @Override
    public EmailNotification getEmailNotification() {
        return EmailNotification.BOOKING_REQUEST_LINE_MANAGER;
    }

    @Override
    public Map<String, String> getPersonalisation() {
        return Map.of(
                "recipient", recipient,
                "learnerName", learnerName,
                "learnerEmail", learnerEmail,
                "courseTitle", courseTitle,
                "courseDate", courseDate,
                "courseLocation", courseLocation,
                "cost", costInPounds,
                "bookingReference", bookingReference
        );
    }
}
