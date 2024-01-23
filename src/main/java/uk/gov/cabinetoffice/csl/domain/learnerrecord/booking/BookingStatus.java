package uk.gov.cabinetoffice.csl.domain.learnerrecord.booking;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

public enum BookingStatus implements Serializable {
    REQUESTED("Requested"), CONFIRMED("Confirmed"), CANCELLED("Cancelled");

    private String value;

    BookingStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
