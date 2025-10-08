package uk.gov.cabinetoffice.csl.service.messaging.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@ToString
public class Message<T extends IMessageMetadata> implements Serializable {
    private final String messageId;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime messageTimestamp;
    private final T metadata;

    @JsonIgnore
    public String getQueue() {
        return this.metadata.getQueue();
    }
}
