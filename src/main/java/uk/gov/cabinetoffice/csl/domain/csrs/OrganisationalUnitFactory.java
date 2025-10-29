package uk.gov.cabinetoffice.csl.domain.csrs;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.csrs.model.AgencyTokenDTO;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitOverview;

import java.util.List;

@Service
public class OrganisationalUnitFactory {

    private final AgencyTokenFactory agencyTokenFactory;

    public OrganisationalUnitFactory(AgencyTokenFactory agencyTokenFactory) {
        this.agencyTokenFactory = agencyTokenFactory;
    }

    public OrganisationalUnitOverview createOrganisationalUnitOverview(OrganisationalUnit organisationalUnit, boolean newOrganisation) {

        AgencyTokenDTO agencyTokenDTO = organisationalUnit.getAgencyToken() == null ? null : agencyTokenFactory.createAgencyTokenDTO(organisationalUnit.getAgencyToken(), newOrganisation);

        return new OrganisationalUnitOverview(
                organisationalUnit.getId(), organisationalUnit.getName(), organisationalUnit.getCode(),
                organisationalUnit.getAbbreviation(), organisationalUnit.getParentId(), organisationalUnit.getParentName(), organisationalUnit.getDomains(),
                agencyTokenDTO
        );
    }

    public OrganisationalUnitMap buildOrganisationalUnits(List<OrganisationalUnit> organisationalUnits) {
        return OrganisationalUnitMap.create(organisationalUnits);
    }

}
