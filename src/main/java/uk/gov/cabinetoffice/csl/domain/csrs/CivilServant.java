package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CivilServant {
    private String fullName;
    private String email;
    private String uid;
    private Grade grade;
    private OrganisationalUnit organisationalUnit;
    private AreaOfWork profession;
    private String lineManagerEmail;
    private String lineManagerName;

    public List<OrganisationalUnit> getDepartmentHierarchy() {
        if (organisationalUnit == null) {
            return new ArrayList<>();
        }
        ArrayList<OrganisationalUnit> orgs = new ArrayList<>(Collections.singletonList(organisationalUnit));
        OrganisationalUnit parent = organisationalUnit.getParent();
        while (parent != null) {
            orgs.add(parent);
            parent = parent.getParent();
        }
        return orgs;
    }
}
