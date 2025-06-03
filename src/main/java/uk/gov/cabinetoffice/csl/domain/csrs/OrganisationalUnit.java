package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganisationalUnit {
    private Long id;
    private String name;
    private String code;
    private String abbreviation;
    private String formattedName;
    private String href;
    private Long parentId;
    private OrganisationalUnit parent;
    private List<OrganisationalUnit> children;
    private List<Domain> domains;
    private AgencyToken agencyToken;

    public OrganisationalUnit(Long id, String name, String code, String abbreviation, OrganisationalUnit parent) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.abbreviation = abbreviation;
        this.parent = parent;
    }
}
