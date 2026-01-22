package uk.gov.cabinetoffice.csl.service.notification.messages;

import lombok.Getter;

import java.util.Map;

@Getter
public class RequestBookingMessageParams implements IEmail {

    private final String recipient;
    private final String courseTitle;
    private final String courseDate;
    private final String courseLocation;
    private final String accessibility;
    private final String bookingReference;

    public RequestBookingMessageParams(String recipient, String courseTitle, String courseDate, String courseLocation, String accessibility, String bookingReference) {
        this.recipient = recipient;
        this.courseTitle = courseTitle;
        this.courseDate = courseDate;
        this.courseLocation = courseLocation;
        this.accessibility = accessibility;
        this.bookingReference = bookingReference;
    }

    @Override
    public EmailNotification getEmailNotification() {
        return EmailNotification.BOOKING_REQUESTED;
    }

    @Override
    public Map<String, String> getPersonalisation() {
        return Map.of(
                "learnerName", recipient,
                "courseTitle", courseTitle,
                "courseDate", courseDate,
                "courseLocation", courseLocation,
                "accessibility", accessibility,
                "bookingReference", bookingReference
        );
    }
}
