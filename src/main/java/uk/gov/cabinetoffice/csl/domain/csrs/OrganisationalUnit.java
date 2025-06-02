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
    protected String formattedName;
    private Long parentId;
    private OrganisationalUnit parent;
    private List<OrganisationalUnit> children;
    private List<DomainDto> domains;
}
