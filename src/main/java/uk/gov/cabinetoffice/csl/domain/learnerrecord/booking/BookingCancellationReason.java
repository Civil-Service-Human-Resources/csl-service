package uk.gov.cabinetoffice.csl.domain.learnerrecord.booking;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = BookingCancellationReasonDeserializer.class)
public enum BookingCancellationReason {
    PAYMENT("the booking has not been paid"),
    REQUESTED("the learner has requested that the booking be cancelled"),
    BEREAVEMENT("Family bereavement"),
    ILLNESS("Illness"),
    PRIORITIES("Other work priorities");

    private final String value;

    BookingCancellationReason(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

