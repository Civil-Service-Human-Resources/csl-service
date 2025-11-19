package uk.gov.cabinetoffice.csl.controller.csrs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.csrs.AgencyToken;
import uk.gov.cabinetoffice.csl.domain.csrs.Domain;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganisationalUnitOverview {

    private Long id;
    private String name;
    private String code;
    private String abbreviation;
    private Long parentId;
    private String parentName;
    private List<Domain> domains;
    private AgencyToken agencyToken;
}
