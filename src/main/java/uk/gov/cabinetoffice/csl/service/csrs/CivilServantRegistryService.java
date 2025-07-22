package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.controller.model.FormattedOrganisationalUnitsParams;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CivilServantRegistryService {

    private final ICSRSClient civilServantRegistryClient;
    private final OrganisationalUnitListService organisationalUnitListService;

    public List<AreaOfWork> getAreasOfWork() {
        return civilServantRegistryClient.getAreasOfWork();
    }

    @Cacheable("organisations-formatted")
    public FormattedOrganisationalUnitNames getFormattedOrganisationalUnitNames(FormattedOrganisationalUnitsParams formattedOrganisationalUnitsParams) {
        log.info("Getting formatted organisational unit names");

        List<OrganisationalUnit> organisationList = organisationalUnitListService.getAllOrganisationalUnitsWithChildren().getOrganisationalUnits();

        if(formattedOrganisationalUnitsParams.getOrganisationId() != null){
            organisationList = organisationList.stream().filter(organisationalUnit -> formattedOrganisationalUnitsParams.getOrganisationId().contains(organisationalUnit.getId().intValue())).toList();
        }

        if(formattedOrganisationalUnitsParams.getDomain() != null){
            organisationList = organisationList.stream().filter(org -> org.hasDomain(formattedOrganisationalUnitsParams.getDomain())).toList();
        }

        FormattedOrganisationalUnitNames formattedOrganisationalUnitNames = new FormattedOrganisationalUnitNames(organisationList
                .stream()
                .map(o -> new FormattedOrganisationalUnitName(o.getId(), o.getFormattedName()))
                .sorted(Comparator.comparing(FormattedOrganisationalUnitName::getName))
                .toList());

        return formattedOrganisationalUnitNames;
    }

    @CacheEvict(value = "organisations-formatted", allEntries = true)
    public void removeFormattedOrganisationsFromCache() {
        log.info("Formatted organisations are removed from the cache.");
    }
}
