package uk.gov.cabinetoffice.csl.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BookingStatus {
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
