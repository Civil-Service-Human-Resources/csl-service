package uk.gov.cabinetoffice.csl.client.csrs;

import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitDto;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnitMap;
import uk.gov.cabinetoffice.csl.service.ITaxonomyMapCacheClient;

public interface IOrganisationalUnitClient extends ITaxonomyMapCacheClient<OrganisationalUnit, OrganisationalUnitMap, OrganisationalUnitDto> {

}
