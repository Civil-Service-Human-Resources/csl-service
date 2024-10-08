package uk.gov.cabinetoffice.csl.domain.rustici;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.csrs.BasicOrganisationalUnit;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto {

    protected String learnerLastName;
    @NotNull
    @NotEmpty
    @Email
    protected String learnerEmail;
    @NotNull
    @NotBlank
    protected String learnerName;
    @NotNull
    @Min(1)
    protected Integer professionId;
    @NotNull
    @NotEmpty
    protected String professionName;

    protected String lineManagerName;
    protected String lineManagerEmail;
    protected Integer gradeId;
    protected String gradeName;

    @NotNull
    @Size(min = 1, message = "At least one department is required in the hierarchy")
    protected ArrayList<BasicOrganisationalUnit> departmentHierarchy;

}
