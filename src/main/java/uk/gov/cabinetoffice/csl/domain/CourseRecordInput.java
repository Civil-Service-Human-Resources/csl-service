package uk.gov.cabinetoffice.csl.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import uk.gov.cabinetoffice.csl.annotations.ValidEnum;

import java.util.List;

import static uk.gov.cabinetoffice.csl.domain.State.IN_PROGRESS;

@Data
public class CourseRecordInput {

    @NotBlank(message = "courseId is required")
    private String courseId;

    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "courseTitle is required")
    private String courseTitle;

    @ValidEnum(enumClass = State.class)
    private String state = IN_PROGRESS.name();

    private Boolean isRequired = false;

    @ValidEnum(enumClass = Preference.class)
    private String preference;

    @Valid
    private List<ModuleRecordInput> moduleRecords;
}
