package uk.gov.cabinetoffice.csl.domain.rustici;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordInput;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleLaunchLinkInput {

    @NotBlank(message = "learnerFirstName is required")
    private String learnerFirstName;

    private String learnerLastName;

    private CourseRecordInput courseRecordInput;
}
