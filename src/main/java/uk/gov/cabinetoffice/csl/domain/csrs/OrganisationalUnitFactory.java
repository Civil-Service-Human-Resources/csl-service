package uk.gov.cabinetoffice.csl.domain.csrs;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitOverview;
import uk.gov.cabinetoffice.csl.service.ITaxonomyItemFactory;

@Service
public class OrganisationalUnitFactory implements ITaxonomyItemFactory<OrganisationalUnit, OrganisationalUnitOverview> {

    private final AgencyTokenFactory agencyTokenFactory;

    public OrganisationalUnitFactory(AgencyTokenFactory agencyTokenFactory) {
        this.agencyTokenFactory = agencyTokenFactory;
    }

    public OrganisationalUnitOverview createOverview(OrganisationalUnit organisationalUnit) {
        return createOverview(organisationalUnit, true);
    }

    public OrganisationalUnitOverview createOverview(OrganisationalUnit organisationalUnit, boolean includeAgencyCapacityUsed) {

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
