package uk.gov.cabinetoffice.csl.domain.learnerrecord.booking;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class BookingCancellationReasonDeserializer extends JsonDeserializer<BookingCancellationReason> {
    @Override
    public BookingCancellationReason deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText();
        for (BookingCancellationReason reason : BookingCancellationReason.values()) {
            if (reason.getValue().equals(value) || reason.name().equals(value)) {
                return reason;
            }
        }
        return null;
    }
}
