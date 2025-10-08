package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.controller.model.DomainResponse;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationalUnitsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.messaging.MessageMetadataFactory;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.RegisteredLearnerOrganisationDeleteMessage;

import java.util.*;
import java.util.stream.Collectors;

import static com.azure.core.util.CoreUtils.isNullOrEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisationalUnitService {

    private final OrganisationalUnitMapCache organisationalUnitMapCache;
    private final ICSRSClient civilServantRegistryClient;
    private final MessageMetadataFactory messageMetadataFactory;
    private final IMessagingClient messagingClient;

    public OrganisationalUnitMap getOrganisationalUnitMap() {
        OrganisationalUnitMap map = organisationalUnitMapCache.get();
        if (map == null) {
            map = civilServantRegistryClient.getAllOrganisationalUnits();
            organisationalUnitMapCache.put(map);
        }
        return map;
    }

    public OrganisationalUnits getAllOrganisationalUnits() {
        log.info("Getting all organisational units");
        return new OrganisationalUnits(getOrganisationalUnitMap().values().stream().toList());
    }

    public FormattedOrganisationalUnitNames getFormattedOrganisationalUnitNames(OrganisationalUnitsParams params) {
        OrganisationalUnitMap allOrganisationalUnits = getOrganisationalUnitMap();
        Set<OrganisationalUnit> filtered = new HashSet<>();
        if (params.shouldGetAll()) {
            filtered.addAll(allOrganisationalUnits.values());
        } else {
            for (OrganisationalUnit organisationalUnit : allOrganisationalUnits.values()) {
                boolean add = false;
                if (params.hasOrganisationIds(organisationalUnit.getId())) {
                    add = true;
                } else if (!isNullOrEmpty(params.getDomain()) && organisationalUnit.hasDomain(params.getDomain())) {
                    if (params.isTierOne()) {
                        Long currentParentId = organisationalUnit.getParentId();
                        while (currentParentId != null) {
                            OrganisationalUnit currentParent = allOrganisationalUnits.get(currentParentId);
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
        return getOrganisationalUnitMap()
                .getMultiple(organisationIds, true);
    }

    public List<Long> getOrganisationsIdsIncludingParentAndChildren(List<Long> organisationIds) {
        return getOrganisationsWithChildrenAsFlatList(organisationIds)
                .stream().map(OrganisationalUnit::getId).collect(Collectors.toList());
    }

    public Map<Long, List<OrganisationalUnit>> getOrganisationsWithChildrenAsFlatListMap(List<Long> organisationIds) {
        Map<Long, List<OrganisationalUnit>> response = new HashMap<>();
        OrganisationalUnitMap organisationalUnitMap = getOrganisationalUnitMap();
        organisationIds.forEach(id -> response.put(id, organisationalUnitMap.getMultiple(List.of(id), true)));
        return response;
    }

    public Map<Long, List<OrganisationalUnit>> getHierarchies(List<Long> organisationIds) {
        return getOrganisationalUnitMap().getHierarchies(organisationIds);
    }

    public void deleteOrganisationalUnit(Long organisationalUnitId) {
        civilServantRegistryClient.deleteOrganisationalUnit(organisationalUnitId);
        removeOrganisationalUnitAndChildrenFromCache(organisationalUnitId);
        updateReportingData(getOrganisationsIdsIncludingParentAndChildren(List.of(organisationalUnitId)));
    }

    private void updateReportingData(List<Long> organisationIds) {
        log.debug("updateReportingData:organisationalUnitIds: {}", organisationIds);
        RegisteredLearnerOrganisationDeleteMessage message = messageMetadataFactory.generateRegisteredLearnersOrganisationDeleteMessage(organisationIds);
        messagingClient.sendMessages(List.of(message));
    }

    public void removeAllOrganisationalUnitsFromCache() {
        organisationalUnitMapCache.evict();
        log.info("Organisations are removed from the cache.");
    }

    public void removeOrganisationalUnitAndChildrenFromCache(Long organisationalUnitId) {
        log.info("Removing organisationalUnit and its children FromCache for the organisationalUnitId: {}.", organisationalUnitId);
        List<Long> organisationIds = getOrganisationsIdsIncludingParentAndChildren(List.of(organisationalUnitId));
        OrganisationalUnitMap map = getOrganisationalUnitMap();
        map.remove(organisationIds);
        organisationalUnitMapCache.put(map);
    }

    private void updateOrganisationalUnits(List<Long> ids, IOrganisationalUnitUpdate update) {
        OrganisationalUnitMap map = getOrganisationalUnitMap();
        ids.forEach(id -> update.apply(map.get(id)));
        organisationalUnitMapCache.put(map);
    }

    public DomainResponse addDomainToOrganisationalUnit(Long organisationUnitId, String domain) {
        UpdateDomainResponse response = civilServantRegistryClient.addDomainToOrganisation(organisationUnitId, domain);
        List<Long> updatedIds = response.getUpdatedChildOrganisationIds();
        updateOrganisationalUnits(updatedIds, organisationalUnit -> {
            organisationalUnit.addDomainAndSort(response.getDomain());
            return organisationalUnit;
        });
        return new DomainResponse(updatedIds);
    }

    public DomainResponse removeDomainFromOrganisationalUnit(Long organisationUnitId, Long domainId, boolean cascade) {
        UpdateDomainResponse response = civilServantRegistryClient.deleteDomain(organisationUnitId, domainId, cascade);
        List<Long> updatedIds = response.getUpdatedChildOrganisationIds();
        updateOrganisationalUnits(updatedIds, organisationalUnit -> {
            organisationalUnit.removeDomain(domainId);
            return organisationalUnit;
        });
        return new DomainResponse(updatedIds);
    }
}
