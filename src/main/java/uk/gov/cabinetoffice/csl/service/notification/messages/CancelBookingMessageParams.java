package uk.gov.cabinetoffice.csl.service.notification.messages;

import lombok.Getter;

import java.util.Map;

@Getter
public class CancelBookingMessageParams implements IEmail {

    private final String recipient;
    private final String cancellationReason;
    private final String accessibility;
    private final String bookingReference;
    private final String courseTitle;
    private final String courseDate;
    private final String courseLocation;

    public CancelBookingMessageParams(String recipient, String cancellationReason, String accessibility, String bookingReference, String courseTitle, String courseDate, String courseLocation) {
        this.recipient = recipient;
        this.cancellationReason = cancellationReason;
        this.accessibility = accessibility;
        this.bookingReference = bookingReference;
        this.courseTitle = courseTitle;
        this.courseDate = courseDate;
        this.courseLocation = courseLocation;
    }

    @Override
    public EmailNotification getEmailNotification() {
        return EmailNotification.CANCEL_BOOKING;
    }

    @Override
    public Map<String, String> getPersonalisation() {
        return Map.of(
                "cancellationReason", cancellationReason,
                "courseTitle", courseTitle,
                "courseDate", courseDate,
                "courseLocation", courseLocation,
                "accessibility", accessibility,
                "bookingReference", bookingReference
        );
    }
}
