package uk.gov.cabinetoffice.csl.service.notification.messages;

import lombok.Getter;

@Getter
public enum EmailNotification {

    NOTIFY_LINE_MANAGER_COMPLETED_LEARNING("notifyLineManagerCompletedLearning"),
    NOTIFY_LEARNER_CANCELLED_EVENT("notifyLearnerEventCancelled"),
    BOOKING_CONFIRMED_LINE_MANAGER("notifyBookingConfirmedLineManager"),
    BOOKING_CONFIRMED("notifyLearnerBookingConfirmed"),
    BOOKING_REQUEST_LINE_MANAGER("notifyBookingRequestLineManager"),
    BOOKING_REQUESTED("notifyLearnerBookingRequested"),
    ;

    private final String configName;

    EmailNotification(String configName) {
        this.configName = configName;
    }
}
