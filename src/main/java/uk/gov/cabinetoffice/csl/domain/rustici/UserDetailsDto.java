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
    @NotNull
    @Min(1)
    protected Integer professionId;
    @NotNull
    @Min(1)
    protected Integer gradeId;

    @NotNull
    @Size(min = 1, message = "At least one department is required in the hierarchy")
    protected Collection<String> userDepartmentHierarchy;

}
