package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CivilServant {
    private String fullName;
    private String email;
    private String uid;
    private Grade grade;
    private OrganisationalUnit organisationalUnit;
    private Profession profession;
    private Collection<String> departmentHierarchy;
}
