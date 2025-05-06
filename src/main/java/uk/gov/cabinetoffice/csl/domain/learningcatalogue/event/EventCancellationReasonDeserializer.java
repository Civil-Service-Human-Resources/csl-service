package uk.gov.cabinetoffice.csl.domain.learningcatalogue.event;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class EventCancellationReasonDeserializer extends JsonDeserializer<EventCancellationReason> {
    @Override
    public EventCancellationReason deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText();
        for (EventCancellationReason reason : EventCancellationReason.values()) {
            if (reason.getValue().equals(value) || reason.name().equals(value)) {
                return reason;
            }
        }
        return null;
    }
}
