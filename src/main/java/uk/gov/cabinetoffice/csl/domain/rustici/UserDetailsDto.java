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
    protected Integer professionId;
    @NotNull
    @NotEmpty
    protected String professionName;

    // Grade is the only profile setting that can be null, so let's not validate it
    protected Integer gradeId;
    protected String gradeName;

    @NotNull
    @Size(min = 1, message = "At least one department is required in the hierarchy")
    protected ArrayList<BasicOrganisationalUnit> departmentHierarchy;

    public Integer getOrganisationId() {
        return departmentHierarchy.stream().findFirst().map(BasicOrganisationalUnit::getId).orElse(0);
    }

    public String getOrganisationName() {
        return departmentHierarchy.stream().findFirst().map(BasicOrganisationalUnit::getName).orElse("");
    }

}
