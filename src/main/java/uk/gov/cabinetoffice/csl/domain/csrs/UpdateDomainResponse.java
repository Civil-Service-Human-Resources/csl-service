package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateDomainResponse {
    private Domain domain;
    private List<Long> updatedChildOrganisationIds;
    private List<Long> skippedChildOrganisationIds;
}
