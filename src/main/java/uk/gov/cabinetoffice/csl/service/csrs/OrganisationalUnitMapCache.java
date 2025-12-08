package uk.gov.cabinetoffice.csl.service.csrs;

import org.springframework.cache.Cache;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnitMap;
import uk.gov.cabinetoffice.csl.util.BasicFetchedCache;

public class OrganisationalUnitMapCache extends BasicFetchedCache<OrganisationalUnitMap> {

    public OrganisationalUnitMapCache(Cache cache, ICSRSClient client) {
        super(cache, "organisationalUnitMap", OrganisationalUnitMap.class, client);
    }

}
