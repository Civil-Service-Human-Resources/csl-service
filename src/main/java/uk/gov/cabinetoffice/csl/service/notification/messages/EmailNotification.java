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
    BOOKING_CANCELLED_LINE_MANAGER("notifyBookingCancelledLineManager"),
    CANCEL_BOOKING("notifyLearnerCancelBooking"),
    INVITE_LEARNER("notifyLearnerInviteLearner"),
    ;

    private final String configName;

    EmailNotification(String configName) {
        this.configName = configName;
    }
}
