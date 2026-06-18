package uk.gov.cabinetoffice.csl.service.csrs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitDto;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitOverview;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnitMap;
import uk.gov.cabinetoffice.csl.service.CachedTaxonomyMapService;
import uk.gov.cabinetoffice.csl.service.ITaxonomyItemFactory;
import uk.gov.cabinetoffice.csl.service.ITaxonomyMapCacheClient;

@Service
public class OrganisationalUnitMapService extends CachedTaxonomyMapService<OrganisationalUnit, OrganisationalUnitMap, OrganisationalUnitDto, OrganisationalUnitOverview> {

    public OrganisationalUnitMapService(@Qualifier("organisations") Cache cache, ITaxonomyItemFactory<OrganisationalUnit,
            OrganisationalUnitOverview> taxonomyItemFactory, ITaxonomyMapCacheClient<OrganisationalUnit,
            OrganisationalUnitMap, OrganisationalUnitDto> client) {
        super(cache, "organisationalUnitMap", OrganisationalUnitMap.class, taxonomyItemFactory, client);
    }
}
