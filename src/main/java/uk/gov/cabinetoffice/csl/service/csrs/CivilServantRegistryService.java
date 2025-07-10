package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.controller.model.FormattedOrganisationalUnitsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.FormattedOrganisationalUnitName;
import uk.gov.cabinetoffice.csl.domain.csrs.FormattedOrganisationalUnitNames;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnits;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
@Slf4j
@AllArgsConstructor
public class CivilServantRegistryService {

    private final ICSRSClient civilServantRegistryClient;

    @Cacheable("organisations")
    public OrganisationalUnits getOrganisationalUnitsByIds() {
        log.info("Getting all organisational units");
        return new OrganisationalUnits(setFormattedName(civilServantRegistryClient.getAllOrganisationalUnits()));
    }

    @Cacheable("organisations-by-ids")
    public OrganisationalUnits getOrganisationalUnitsByIds(Integer[] ids, boolean fetchChildren) {
        log.info("Getting organisational units by IDs: " + ids.toString());
        return new OrganisationalUnits(setFormattedName(civilServantRegistryClient.getOrganisationalUnitsById(ids, fetchChildren)));
    }

    @CacheEvict(value = "organisations", allEntries = true)
    public void removeOrganisationsFromCache() {
        log.info("Organisations are removed from the cache.");
    }

    @CacheEvict(value = "organisations-by-ids", allEntries = true)
    public void removeOrganisationsByIdsFromCache() {
        log.info("Organisations by ID are removed from the cache.");
    }

    @Cacheable("organisations-formatted")
    public FormattedOrganisationalUnitNames getFormattedOrganisationalUnitNames(FormattedOrganisationalUnitsParams formattedOrganisationalUnitsParams) {
        log.info("Getting formatted organisational unit names");
        OrganisationalUnits organisationalUnits;
        if(formattedOrganisationalUnitsParams.getOrganisationId() == null){
            organisationalUnits = getOrganisationalUnitsByIds();
        }
        else{
            organisationalUnits = getOrganisationalUnitsByIds(formattedOrganisationalUnitsParams.getOrganisationId(), false);
        }

        List<OrganisationalUnit> organisationList = organisationalUnits.getOrganisationalUnits();

        if(formattedOrganisationalUnitsParams.getDomain() != null){
            organisationList = organisationList.stream().filter(org -> org.hasDomain(formattedOrganisationalUnitsParams.getDomain())).toList();
        }

        return new FormattedOrganisationalUnitNames(organisationList
                .stream()
                .map(o -> new FormattedOrganisationalUnitName(o.getId(), o.getFormattedName()))
                .sorted(Comparator.comparing(FormattedOrganisationalUnitName::getName))
                .toList());
    }

    @CacheEvict(value = "organisations-formatted", allEntries = true)
    public void removeFormattedOrganisationsFromCache() {
        log.info("Formatted organisations are removed from the cache.");
    }

    private List<OrganisationalUnit> setFormattedName(List<OrganisationalUnit> allOrganisationalUnits) {
        Map<Long, OrganisationalUnit> orgMap = civilServantRegistryClient.getAllOrganisationalUnits().stream()
                .collect(toMap(OrganisationalUnit::getId, o -> o));

        return allOrganisationalUnits.stream()
                .peek(o -> {
                    StringBuilder formattedName = new StringBuilder(o.getName());
                    Long parentId = o.getParentId();
                    while(parentId != null) {
                        OrganisationalUnit parentOrganisationalUnit = orgMap.get(parentId);
                        String parentName = parentOrganisationalUnit.getName();
                        formattedName.insert(0, parentName + " | ");
                        parentId = parentOrganisationalUnit.getParentId();
                    }
                    o.setFormattedName(formattedName.toString());
                }).toList();
    }
}
