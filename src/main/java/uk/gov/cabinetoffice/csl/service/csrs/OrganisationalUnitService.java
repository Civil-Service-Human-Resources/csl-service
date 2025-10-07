package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationalUnitDto;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationalUnitsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.domain.error.NotFoundException;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.messaging.MessageMetadataFactory;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.RegisteredLearnerOrganisationDeleteMessage;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.RegisteredLearnerOrganisationUpdateMessage;

import java.util.*;
import java.util.stream.Collectors;

import static com.azure.core.util.CoreUtils.isNullOrEmpty;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
        log.info("Deleting organisational unit for id: {}", organisationalUnitId);
        OrganisationalUnitMap organisationalUnitMap = getOrganisationalUnitMap();
        List<OrganisationalUnit> organisationalUnitsToBeRemoved = organisationalUnitMap.getMultiple(Collections.singleton(organisationalUnitId), true);
        List<Long> organisationalUnitIdsToBeRemoved = organisationalUnitsToBeRemoved.stream().map(OrganisationalUnit::getId).toList();
        civilServantRegistryClient.deleteOrganisationalUnit(organisationalUnitId);
        removeOrganisationalUnitAndChildrenFromCache(organisationalUnitIdsToBeRemoved);
        deleteFromReportingData(organisationalUnitIdsToBeRemoved);
    }

    private void deleteFromReportingData(List<Long> organisationIds) {
        log.info("deleteFromReportingData:organisationalUnitIds: {}", organisationIds);
        RegisteredLearnerOrganisationDeleteMessage message = messageMetadataFactory.generateRegisteredLearnersOrganisationDeleteMessage(organisationIds);
        messagingClient.sendMessages(List.of(message));
    }

    public void removeOrganisationalUnitAndChildrenFromCache(List<Long> organisationIds) {
        log.info("Removing organisationalUnits and its children from cache for the organisationalUnitIds: {}", organisationIds);
        OrganisationalUnitMap organisationalUnitMap = organisationalUnitMapCache.get();
        organisationIds.forEach(organisationalUnitMap::remove);
        organisationalUnitMapCache.put(organisationalUnitMap);
    }

    public void removeAllOrganisationalUnitsFromCache() {
        organisationalUnitMapCache.evict();
        log.info("Organisations are removed from the cache.");
    }

    public void patchOrganisationalUnit(Long organisationalUnitId, OrganisationalUnitDto organisationalUnitDto) {
        log.info("Updating organisational unit data: {} for organisationalUnitId: {}", organisationalUnitDto, organisationalUnitId);
        civilServantRegistryClient.patchOrganisationalUnit(organisationalUnitId, organisationalUnitDto);
        log.info("Updating organisational unit data in cache: {} for organisationalUnitId: {}", organisationalUnitDto, organisationalUnitId);
        List<OrganisationalUnit> multipleOrgs = updateOrganisationalUnitsInCache(organisationalUnitId, organisationalUnitDto);
        log.info("Updating organisational units formatted name in reporting for organisationalUnits: {}", multipleOrgs);
        updateReportingData(multipleOrgs);
    }

    private void updateReportingData(List<OrganisationalUnit> multipleOrgs) {
        RegisteredLearnerOrganisationUpdateMessage message = messageMetadataFactory.generateRegisteredLearnersOrganisationUpdateMessage(multipleOrgs);
        log.info("Sending organisational unit message to update reporting data: {}", message);
        messagingClient.sendMessages(List.of(message));
    }

    public List<OrganisationalUnit> updateOrganisationalUnitsInCache(Long organisationalUnitId, OrganisationalUnitDto organisationalUnitDto) {
        OrganisationalUnitMap organisationalUnitMap = getOrganisationalUnitMap();
        OrganisationalUnit organisationalUnit = organisationalUnitMap.get(organisationalUnitId);
        if (organisationalUnit == null) {
            log.error("OrganisationalUnit not found for id: {}", organisationalUnitId);
            throw new NotFoundException("OrganisationalUnit not found for id: " + organisationalUnitId);
        }

        Long originalParentId = organisationalUnit.getParentId();
        String newParentIdStr = organisationalUnitDto.getParent();
        Long newParentId = isNotBlank(newParentIdStr) ? Long.parseLong(newParentIdStr) : null;
        OrganisationalUnit newParent = organisationalUnitMap.get(newParentId);
        organisationalUnit.setParent(newParent);
        organisationalUnit.setParentId(newParent != null ? newParent.getId() : null);
        organisationalUnit.setAbbreviation(organisationalUnitDto.getAbbreviation());
        organisationalUnit.setCode(organisationalUnitDto.getCode());
        organisationalUnit.setName(organisationalUnitDto.getName());

        List<OrganisationalUnit> multipleOrgs = organisationalUnitMap.getMultiple(singletonList(organisationalUnitId), true);
        List<OrganisationalUnit> updatedOrganisationalUnits = organisationalUnitMap.updateFormattedName(multipleOrgs);
        updatedOrganisationalUnits.forEach(u -> organisationalUnitMap.put(u.getId(), u));
        if(originalParentId != null) {
            OrganisationalUnit originalParent = organisationalUnitMap.get(originalParentId);
            originalParent.getChildIds().remove(organisationalUnitId);
            organisationalUnitMap.put(originalParent.getId(), originalParent);
        }
        organisationalUnitMapCache.put(organisationalUnitMap);
        return updatedOrganisationalUnits;
    }
}
