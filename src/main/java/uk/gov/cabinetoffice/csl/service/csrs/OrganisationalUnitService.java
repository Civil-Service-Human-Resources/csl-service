package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.controller.csrs.model.*;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.domain.error.ValidationException;
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

    public GetOrganisationalUnits getOrganisationalUnitOverview(GetOrganisationUnitsFilter params) {
        OrganisationalUnitMap allOrganisationalUnits = getOrganisationalUnitMap();
        Set<OrganisationalUnitOverview> filtered = params.getOrganisationId()
                .stream().flatMap(id -> {
                    ArrayList<OrganisationalUnit> organisationalUnits = new ArrayList<>();
                    OrganisationalUnit organisationalUnit = allOrganisationalUnits.get(id);
                    organisationalUnits.add(organisationalUnit);
                    if (params.isIncludeParents()) {
                        Long currentParentId = organisationalUnit.getParentId();
                        while (currentParentId != null) {
                            OrganisationalUnit currentParent = allOrganisationalUnits.get(currentParentId);
                            organisationalUnits.add(currentParent);
                            currentParentId = currentParent.getParentId();
                        }
                    }
                    return organisationalUnits.stream().map(organisationalUnitFactory::createOrganisationalUnitOverview);
                }).collect(Collectors.toSet());

        return new GetOrganisationalUnits(filtered.stream().sorted(Comparator.comparing(OrganisationalUnitOverview::getName, String::compareToIgnoreCase)).toList());
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
                .sorted(Comparator.comparing(FormattedOrganisationalUnitName::getName, String::compareToIgnoreCase))
                .toList());
    }

    public List<Long> getOrganisationIdsWithChildrenAsFlatList(List<Long> organisationIds) {
        return getOrganisationalUnitMap().getMultipleAsIds(organisationIds, true);
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
        OrganisationalUnitMap organisationalUnitMap = getOrganisationalUnitMap();
        if (organisationalUnitDto.getParentId() != null) {
            if (organisationalUnitMap.getMultipleAsIds(List.of(organisationalUnitId), true)
                    .contains(organisationalUnitDto.getParentId())) {
                throw new ValidationException("Can't set a parent organisation to a child in the same hierarchy");
            }
        }
        civilServantRegistryClient.patchOrganisationalUnit(organisationalUnitId, organisationalUnitDto);
        OrganisationalUnit organisationalUnit = organisationalUnitMap.get(organisationalUnitId);
        organisationalUnit.setCode(organisationalUnitDto.getCode());
        boolean requiresRebuild = false;
        if (!organisationalUnit.getParentIdSafe().equals(organisationalUnitDto.getParentIdSafe())) {
            requiresRebuild = true;
            if (organisationalUnit.getParentId() != null) {
                organisationalUnitMap.updateOrganisationalUnit(organisationalUnit.getParentId(), o -> {
                    o.getChildIds().remove(organisationalUnitId);
                    return o;
                });
            }
            Optional.ofNullable(organisationalUnitDto.getParentId())
                    .map(newParentIdStr -> organisationalUnitMap.get(organisationalUnitDto.getParentId()))
                    .ifPresentOrElse(newParent -> {
                        organisationalUnit.setParentId(newParent.getId());
                        organisationalUnit.setParentName(newParent.getName());
                    }, () -> {
                        organisationalUnit.setParentId(null);
                        organisationalUnit.setParentName(null);
                    });
        }

        if (!(organisationalUnit.getAbbreviationSafe().equals(organisationalUnitDto.getAbbreviationSafe())
                || organisationalUnit.getName().equals(organisationalUnitDto.getName()))) {
            requiresRebuild = true;
            organisationalUnit.setAbbreviation(organisationalUnitDto.getAbbreviation());
            organisationalUnit.setName(organisationalUnitDto.getName());
        }

        if (requiresRebuild) {
            List<OrganisationalUnit> updatedOrganisationalUnits = organisationalUnitMap.rebuildHierarchy(organisationalUnit);
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
        organisationalUnit = organisationalUnitMap.setOrganisationalUnitData(organisationalUnit);
        organisationalUnitMapCache.put(organisationalUnitMap);
        return organisationalUnitFactory.createOrganisationalUnitOverview(organisationalUnit, true);
    }

}
