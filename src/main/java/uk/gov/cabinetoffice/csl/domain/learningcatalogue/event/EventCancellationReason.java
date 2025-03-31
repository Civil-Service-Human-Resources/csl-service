package uk.gov.cabinetoffice.csl.domain.learningcatalogue.event;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

@Getter
@JsonDeserialize(using = EventCancellationReasonDeserializer.class)
public enum EventCancellationReason {

    UNAVAILABLE("the event is no longer available"), VENUE("short notice unavailability of the venue");

    private final String value;

    EventCancellationReason(String value) {
        this.value = value;
    }
}
