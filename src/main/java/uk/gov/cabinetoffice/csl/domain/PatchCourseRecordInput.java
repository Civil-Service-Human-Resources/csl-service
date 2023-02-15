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
public class PatchCourseRecordInput {

    @ValidEnum(enumClass = State.class)
    private String state;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdated;

    @ValidEnum(enumClass = Preference.class)
    private String preference;

    private boolean isRequired;
}
