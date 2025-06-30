package uk.gov.cabinetoffice.csl.domain.csrs.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.client.model.PagedResponse;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisationalUnitsPagedResponse extends PagedResponse<OrganisationalUnit> {
}
