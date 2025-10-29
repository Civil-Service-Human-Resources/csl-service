package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.controller.csrs.model.*;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.messaging.MessageMetadataFactory;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.RegisteredLearnerOrganisationDeleteMessage;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.RegisteredLearnerOrganisationUpdateMessage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.azure.core.util.CoreUtils.isNullOrEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisationalUnitService {

    private final OrganisationalUnitMapCache organisationalUnitMapCache;
    private final OrganisationalUnitFactory organisationalUnitFactory;
    private final ICSRSClient civilServantRegistryClient;
    private final MessageMetadataFactory messageMetadataFactory;
    private final IMessagingClient messagingClient;

    public OrganisationalUnitMap getOrganisationalUnitMap() {
        return organisationalUnitMapCache.get();
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

    public DeleteOrganisationResponse deleteOrganisationalUnit(Long organisationalUnitId) {
        log.info("Deleting organisational unit for id: {}", organisationalUnitId);
        civilServantRegistryClient.deleteOrganisationalUnit(organisationalUnitId);
        OrganisationalUnitMap organisationalUnitMap = getOrganisationalUnitMap();
        List<Long> organisationalUnitIdsToBeRemoved = organisationalUnitMap.delete(organisationalUnitId);
        organisationalUnitMapCache.put(organisationalUnitMap);
        deleteFromReportingData(organisationalUnitIdsToBeRemoved);
        return new DeleteOrganisationResponse(organisationalUnitIdsToBeRemoved);
    }

    private void deleteFromReportingData(List<Long> organisationIds) {
        log.info("deleteFromReportingData:organisationalUnitIds: {}", organisationIds);
        RegisteredLearnerOrganisationDeleteMessage message = messageMetadataFactory.generateRegisteredLearnersOrganisationDeleteMessage(organisationIds);
        messagingClient.sendMessages(List.of(message));
    }

    public void removeOrganisationalUnitsFromCache(List<Long> organisationIds) {
        log.info("Removing organisationalUnits from cache for the organisationalUnitIds: {}", organisationIds);
        OrganisationalUnitMap organisationalUnitMap = organisationalUnitMapCache.get();
        List<Long> idsRemoved = organisationalUnitMap.delete(organisationIds);
        log.info("Ids removed: {}", idsRemoved);
        organisationalUnitMapCache.put(organisationalUnitMap);
    }

    public void removeAllOrganisationalUnitsFromCache() {
        organisationalUnitMapCache.evict();
        log.info("Organisations are removed from the cache.");
    }

    public OrganisationalUnitOverview patchOrganisationalUnit(Long organisationalUnitId, OrganisationalUnitDto organisationalUnitDto) {
        log.info("Updating organisational unit data: {} for organisationalUnitId: {}", organisationalUnitDto, organisationalUnitId);
        civilServantRegistryClient.patchOrganisationalUnit(organisationalUnitId, organisationalUnitDto);
        OrganisationalUnitMap organisationalUnitMap = getOrganisationalUnitMap();
        OrganisationalUnit organisationalUnit = organisationalUnitMap.get(organisationalUnitId);

        organisationalUnit.setCode(organisationalUnitDto.getCode());
        boolean parentChanged = !organisationalUnit.getParentIdSafe().equals(organisationalUnitDto.getParentIdSafe());
        if (parentChanged) {
            if (organisationalUnit.getParentId() != null) {
                OrganisationalUnit parent = organisationalUnitMap.get(organisationalUnit.getParentId());
                parent.getChildIds().remove(organisationalUnitId);
                organisationalUnitMap.put(organisationalUnit.getParentId(), parent);
            }
            OrganisationalUnit newParent = Optional.ofNullable(organisationalUnitDto.getParentId())
                    .map(newParentIdStr -> organisationalUnitMap.get(organisationalUnitDto.getParentId())).orElse(null);
            organisationalUnit.setParentId(newParent != null ? newParent.getId() : null);
            organisationalUnit.setParentName(newParent != null ? newParent.getName() : null);
        }

        boolean formattedNameChanged = !(organisationalUnit.getAbbreviation().equals(organisationalUnitDto.getAbbreviation())
                || organisationalUnit.getName().equals(organisationalUnitDto.getName()));
        if (formattedNameChanged) {
            organisationalUnit.setAbbreviation(organisationalUnitDto.getAbbreviation());
            organisationalUnit.setName(organisationalUnitDto.getName());
        }

        if (formattedNameChanged || parentChanged) {
            List<OrganisationalUnit> updatedOrganisationalUnits = organisationalUnitMap.buildOrganisationalUnit(organisationalUnitId, true);
            updatedOrganisationalUnits.forEach(u -> organisationalUnitMap.put(u.getId(), u));
            updateReportingData(updatedOrganisationalUnits);
        }
        organisationalUnitMapCache.put(organisationalUnitMap);

        return organisationalUnitFactory.createOrganisationalUnitOverview(organisationalUnit, false);
    }

    private void updateReportingData(List<OrganisationalUnit> multipleOrgs) {
        RegisteredLearnerOrganisationUpdateMessage message = messageMetadataFactory.generateRegisteredLearnersOrganisationUpdateMessage(multipleOrgs);
        log.info("Sending organisational unit message to update reporting data: {}", message);
        messagingClient.sendMessages(List.of(message));
    }

    public List<OrganisationalUnit> updateOrganisationalUnit(Collection<Long> ids, Function<OrganisationalUnit, OrganisationalUnit> update) {
        OrganisationalUnitMap map = getOrganisationalUnitMap();
        List<OrganisationalUnit> results = ids.stream().map(id -> update.apply(map.get(id))).toList();
        organisationalUnitMapCache.put(map);
        return results;
    }

    public DomainResponse addDomainToOrganisationalUnit(Long organisationUnitId, CreateDomainDto domain) {
        UpdateDomainResponse response = civilServantRegistryClient.addDomainToOrganisation(organisationUnitId, domain);
        updateOrganisationalUnit(response.getAllUpdatedIds(), organisationalUnit -> {
            organisationalUnit.addDomainAndSort(response.getDomain());
            return organisationalUnit;
        });
        return new DomainResponse(response.getDomain(), response.getUpdatedChildOrganisationIds());
    }

    public DomainResponse removeDomainFromOrganisationalUnit(Long organisationUnitId, Long domainId, DeleteDomainDto body) {
        UpdateDomainResponse response = civilServantRegistryClient.deleteDomain(organisationUnitId, domainId, body);
        updateOrganisationalUnit(response.getAllUpdatedIds(), organisationalUnit -> {
            organisationalUnit.removeDomain(domainId);
            return organisationalUnit;
        });
        return new DomainResponse(response.getDomain(), response.getUpdatedChildOrganisationIds());
    }

    public OrganisationalUnitTree getOrganisationalUnitTree() {
        return new OrganisationalUnitTree(getOrganisationalUnitMap().getOrganisationalUnitTree());
    }

    public OrganisationalUnitOverview getOrganisationalUnitOverview(Long organisationalUnitId) {
        OrganisationalUnitMap organisationalUnitMap = getOrganisationalUnitMap();
        OrganisationalUnit organisationalUnit = organisationalUnitMap.get(organisationalUnitId);
        return organisationalUnitFactory.createOrganisationalUnitOverview(organisationalUnit, false);
    }

    public OrganisationalUnitOverview createOrganisationalUnit(OrganisationalUnitDto organisationalUnitDto) {
        OrganisationalUnit organisationalUnit = civilServantRegistryClient.createOrganisationalUnit(organisationalUnitDto);
        OrganisationalUnitMap organisationalUnitMap = getOrganisationalUnitMap();
        organisationalUnitMap.buildOrganisationalUnit(organisationalUnit);
        organisationalUnitMapCache.put(organisationalUnitMap);
        return organisationalUnitFactory.createOrganisationalUnitOverview(organisationalUnit, true);
    }

}
