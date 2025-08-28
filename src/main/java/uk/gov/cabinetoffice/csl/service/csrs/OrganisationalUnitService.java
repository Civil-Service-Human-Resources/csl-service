package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationalUnitsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.*;

import java.util.*;

import static com.azure.core.util.CoreUtils.isNullOrEmpty;

@Service
@Slf4j
@AllArgsConstructor
public class OrganisationalUnitService {
    private final ICSRSClient civilServantRegistryClient;

    public OrganisationalUnits getAllOrganisationalUnits() {
        log.info("Getting all organisational units");
        return new OrganisationalUnits(civilServantRegistryClient.getAllOrganisationalUnits().values().stream().toList());
    }

    public FormattedOrganisationalUnitNames getFormattedOrganisationalUnitNames(OrganisationalUnitsParams params) {
        OrganisationalUnitMap allOrgs = civilServantRegistryClient.getAllOrganisationalUnits();
        Set<OrganisationalUnit> filtered = new HashSet<>();
        if (params.shouldGetAll()) {
            filtered.addAll(allOrgs.values());
        } else {
            for (OrganisationalUnit organisationalUnit : allOrgs.values()) {
                boolean add = false;
                if (params.hasOrganisationIds(organisationalUnit.getId())) {
                    add = true;
                } else if (!isNullOrEmpty(params.getDomain()) && organisationalUnit.hasDomain(params.getDomain())) {
                    if (params.isTierOne()) {
                        Long currentParentId = organisationalUnit.getParentId();
                        while (currentParentId != null) {
                            OrganisationalUnit currentParent = allOrgs.get(currentParentId);
                            filtered.add(currentParent);
                            currentParentId = currentParent.getParentId();
                        }
                    }
                    add = true;
                }
                if (add) {
                    filtered.add(organisationalUnit);
                }
            }
        }
        return new FormattedOrganisationalUnitNames(filtered.stream()
                .map(o -> new FormattedOrganisationalUnitName(o.getId(), o.getFormattedName(), o.getCode(), o.getAbbreviation()))
                .sorted(Comparator.comparing(FormattedOrganisationalUnitName::getName))
                .toList());
    }

    public List<OrganisationalUnit> getOrganisationsWithChildrenAsFlatList(List<Long> organisationIds) {
        return civilServantRegistryClient.getAllOrganisationalUnits()
                .getMultiple(organisationIds, true);
    }

    public Map<Long, List<OrganisationalUnit>> getOrganisationsWithChildrenAsFlatListMap(List<Long> organisationIds) {
        Map<Long, List<OrganisationalUnit>> response = new HashMap<>();
        OrganisationalUnitMap organisationalUnitMap = civilServantRegistryClient.getAllOrganisationalUnits();
        organisationIds.forEach(id -> response.put(id, organisationalUnitMap.getMultiple(List.of(id), true)));
        return response;
    }

    @CacheEvict(value = "organisations", allEntries = true)
    public void removeOrganisationsFromCache() {
        log.info("Organisations are removed from the cache.");
    }

    public Map<Long, List<OrganisationalUnit>> getHierarchies(List<Long> organisationIds) {
        return civilServantRegistryClient.getAllOrganisationalUnits().getHierarchies(organisationIds);
    }

    public void deleteOrganisationalUnit(Long organisationUnitId) {
        civilServantRegistryClient.deleteOrganisationalUnit(new OrganisationDTO(organisationUnitId));
        removeOrganisationsFromCache();
        //TODO: Send message to reporting-service to remove OrganisationalUnit from learners-records
    }
}
