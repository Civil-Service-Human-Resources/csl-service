package uk.gov.cabinetoffice.csl.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.annotations.ValidEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude
public class PatchModuleRecordInput {

    private String uid;

    @ValidEnum(enumClass = State.class)
    private String state;

    @ValidEnum(enumClass = Result.class)
    private String result;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime completionDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;

    private String score;

    private boolean rated;

    @ValidEnum(enumClass = BookingStatus.class)
    private String bookingStatus;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime eventDate;

    private String eventId;
}
