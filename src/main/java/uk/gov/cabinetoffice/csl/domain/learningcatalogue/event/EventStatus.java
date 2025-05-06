package uk.gov.cabinetoffice.csl.domain.learningcatalogue.event;

import lombok.Getter;

@Getter
public enum EventStatus {
    ACTIVE("Active"),
    CANCELLED("Cancelled");

    private final String value;

    EventStatus(String value) {
        this.value = value;
    }
}
