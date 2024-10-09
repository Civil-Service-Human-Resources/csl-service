package uk.gov.cabinetoffice.csl.domain.rustici;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

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
    protected Integer organisationId;
    
    protected String organisationAbbreviation;
    @NotNull
    @Min(1)
    protected Integer professionId;
    @NotNull
    @NotEmpty
    protected String professionName;

    // Grade is the only profile setting that can be null, so let's not validate it
    protected Integer gradeId;
    protected String gradeCode;

    @NotNull
    @Size(min = 1, message = "At least one department is required in the hierarchy")
    protected Collection<String> userDepartmentHierarchy;

}
