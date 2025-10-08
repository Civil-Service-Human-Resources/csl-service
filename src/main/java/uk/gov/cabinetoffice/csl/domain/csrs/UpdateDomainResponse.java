package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateDomainResponse {
    private Domain domain;
    private List<Long> updatedChildOrganisationIds;
    private List<Long> skippedChildOrganisationIds;
}
