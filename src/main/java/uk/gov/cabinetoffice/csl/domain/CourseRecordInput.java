package uk.gov.cabinetoffice.csl.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import uk.gov.cabinetoffice.csl.annotations.ValidEnum;

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

    @Valid
    private List<ModuleRecordInput> moduleRecords;
}
