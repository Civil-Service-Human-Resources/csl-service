package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import uk.gov.cabinetoffice.csl.annotations.ValidEnum;

@Data
public class ModuleRecordInput {

    private String uid;

    private String userId;

    private String courseId;

    private String moduleId;

    @NotBlank(message = "ModuleTitle is required")
    private String moduleTitle;

    @NotBlank(message = "optional is required")
    private Boolean optional;

    @NotBlank(message = "moduleType is required")
    private String moduleType;

    @ValidEnum(enumClass = State.class)
    private String state;

    @ValidEnum(enumClass = Result.class)
    private String result;

    private BigDecimal cost;

    private Long duration;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate eventDate;

    private String eventId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updated;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime completedDate;
}
