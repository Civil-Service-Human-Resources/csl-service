package uk.gov.cabinetoffice.csl.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import uk.gov.cabinetoffice.csl.annotations.ValidEnum;

import static uk.gov.cabinetoffice.csl.domain.State.IN_PROGRESS;

@Data
public class ModuleRecordInput {

    private String uid = UUID.randomUUID().toString();

    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "courseId is required")
    private String courseId;

    @NotBlank(message = "moduleId is required")
    private String moduleId;

    @NotBlank(message = "ModuleTitle is required")
    private String moduleTitle;

    @NotNull(message = "optional is required")
    private Boolean optional;

    @NotBlank(message = "moduleType is required")
    private String moduleType;

    @ValidEnum(enumClass = State.class)
    private String state = IN_PROGRESS.name();

    private BigDecimal cost;

    private Long duration;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate eventDate;

    private String eventId;
}
