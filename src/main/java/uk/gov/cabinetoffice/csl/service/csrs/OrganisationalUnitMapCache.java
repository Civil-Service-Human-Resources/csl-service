package uk.gov.cabinetoffice.csl.service.csrs;

import org.springframework.cache.Cache;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnitMap;
import uk.gov.cabinetoffice.csl.util.BasicCache;

public class OrganisationalUnitMapCache extends BasicCache<OrganisationalUnitMap> {
    public OrganisationalUnitMapCache(Cache cache) {
        super(cache, "organisationalUnitMap", OrganisationalUnitMap.class);
    }
}
