package uk.gov.cabinetoffice.csl.domain.csrs;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitOverview;

@Service
public class OrganisationalUnitFactory {

    private final AgencyTokenFactory agencyTokenFactory;

    public OrganisationalUnitFactory(AgencyTokenFactory agencyTokenFactory) {
        this.agencyTokenFactory = agencyTokenFactory;
    }

    public OrganisationalUnitOverview createOrganisationalUnitOverview(OrganisationalUnit organisationalUnit) {
        return createOrganisationalUnitOverview(organisationalUnit, true);
    }

    public OrganisationalUnitOverview createOrganisationalUnitOverview(OrganisationalUnit organisationalUnit, boolean includeAgencyCapacityUsed) {

        AgencyToken agencyToken = organisationalUnit.getAgencyToken();
        if (agencyToken != null && includeAgencyCapacityUsed) {
            agencyToken = agencyTokenFactory.formatAgencyToken(organisationalUnit.getAgencyToken());
        }

        return new OrganisationalUnitOverview(
                organisationalUnit.getId(), organisationalUnit.getName(), organisationalUnit.getCode(),
                organisationalUnit.getAbbreviation(), organisationalUnit.getParentId(), organisationalUnit.getParentName(), organisationalUnit.getDomains(),
                agencyToken
        );
    }

}
