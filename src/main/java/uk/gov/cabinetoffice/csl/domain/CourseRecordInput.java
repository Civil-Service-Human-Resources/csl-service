package uk.gov.cabinetoffice.csl.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import uk.gov.cabinetoffice.csl.annotations.ValidEnum;

import java.time.LocalDate;
import java.util.List;

@Data
public class CourseRecordInput {

    private String courseId;

    private String userId;

    @NotBlank(message = "courseTitle is required")
    private String courseTitle;

    @ValidEnum(enumClass = State.class)
    private String state;

    @NotBlank(message = "isRequired is required")
    private Boolean isRequired = false;

    @ValidEnum(enumClass = Preference.class)
    private String preference;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate previousDueDate;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate nextDueDate;

    @Valid
    private List<ModuleRecordInput> moduleRecords;
}
