package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationalUnitDto;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationalUnitsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.error.NotFoundException;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.messaging.MessageMetadataFactory;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.RegisteredLearnerOrganisationDeleteMessage;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.RegisteredLearnerOrganisationUpdateMessage;

import java.util.*;
import java.util.stream.Collectors;

import static com.azure.core.util.CoreUtils.isNullOrEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisationalUnitService {

    private final OrganisationalUnitMapCache organisationalUnitMapCache;
    private final ICSRSClient civilServantRegistryClient;
    private final MessageMetadataFactory messageMetadataFactory;
    private final IMessagingClient messagingClient;
    private final OrganisationalUnitFactory organisationalUnitFactory;

    public OrganisationalUnitMap getOrganisationalUnitMap() {
        if (organisationalUnitMapCache.get() == null) {
            log.info("Setting organisational units cache");
            organisationalUnitMapCache.put(civilServantRegistryClient.getAllOrganisationalUnits());
        }
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
        return getOrganisationalUnitMap().getMultiple(organisationIds, true);
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
        civilServantRegistryClient.deleteOrganisationalUnit(organisationalUnitId);
        removeOrganisationalUnitAndChildrenFromCache(organisationalUnitId);
        deleteFromReportingData(getOrganisationsIdsIncludingParentAndChildren(List.of(organisationalUnitId)));
    }

    private void deleteFromReportingData(List<Long> organisationIds) {
        log.debug("deleteFromReportingData:organisationalUnitIds: {}", organisationIds);
        RegisteredLearnerOrganisationDeleteMessage message = messageMetadataFactory.generateRegisteredLearnersOrganisationDeleteMessage(organisationIds);
        messagingClient.sendMessages(List.of(message));
    }

    public void patchOrganisationalUnit(Long organisationalUnitId, OrganisationalUnitDto organisationalUnitDto) {
        log.info("Updating organisational unit data in csrs: {} for organisationalUnitId: {}", organisationalUnitDto, organisationalUnitId);
        civilServantRegistryClient.patchOrganisationalUnit(organisationalUnitId, organisationalUnitDto);
        log.info("Updating organisational unit data in cache: {} for organisationalUnitId: {}", organisationalUnitDto, organisationalUnitId);
        updateOrganisationalUnitsInCache(organisationalUnitId, organisationalUnitDto);
        List<OrganisationalUnit> multipleOrgs = getOrganisationsWithChildrenAsFlatList(Collections.singletonList(organisationalUnitId));
        log.info("Updating organisational units formatted name in reporting for organisationalUnits: {}", multipleOrgs);
        updateReportingData(multipleOrgs);
    }

    private void updateReportingData(List<OrganisationalUnit> multipleOrgs) {
        RegisteredLearnerOrganisationUpdateMessage message = messageMetadataFactory.generateRegisteredLearnersOrganisationUpdateMessage(multipleOrgs);
        log.info("Sending organisational unit message to update reporting data: {}", message);
        messagingClient.sendMessages(List.of(message));
    }

    public void removeAllOrganisationalUnitsFromCache() {
        organisationalUnitMapCache.evict();
        log.info("Organisations are removed from the cache.");
    }

    public void removeOrganisationalUnitsFromCache(List<Long> organisationIds) {
        log.info("Removing organisationalUnits from Cache for the organisationalUnitIds: {}", organisationIds);
        organisationIds.forEach(organisationalUnitId -> getOrganisationalUnitMap().remove(organisationalUnitId));
    }

    public void removeOrganisationalUnitAndChildrenFromCache(Long organisationalUnitId) {
        log.info("Removing organisationalUnit and its children from Cache for the organisationalUnitId: {}.", organisationalUnitId);
        removeOrganisationalUnitsFromCache(getOrganisationsIdsIncludingParentAndChildren(List.of(organisationalUnitId)));
    }

    public OrganisationalUnitMap updateOrganisationalUnitsInCache(Long organisationalUnitId, OrganisationalUnitDto organisationalUnitDto) {
        OrganisationalUnitMap organisationalUnitMap = getOrganisationalUnitMap();
        OrganisationalUnit organisationalUnit = organisationalUnitMap.get(organisationalUnitId);
        if (organisationalUnit == null) {
            log.error("OrganisationalUnit not found for id: {}", organisationalUnitId);
            throw new NotFoundException("OrganisationalUnit not found for id: " + organisationalUnitId);
        }

        OrganisationalUnit parent = parseParent(organisationalUnitDto.getParent(), organisationalUnitMap);
        organisationalUnit.setParent(parent);
        organisationalUnit.setParentId(parent != null ? parent.getId() : null);
        organisationalUnit.setAbbreviation(organisationalUnitDto.getAbbreviation());
        organisationalUnit.setCode(organisationalUnitDto.getCode());
        organisationalUnit.setName(organisationalUnitDto.getName());

        List<OrganisationalUnit> organisationalUnits = new ArrayList<>(organisationalUnitMap.values());
        OrganisationalUnitMap rebuiltOrgMap = organisationalUnitFactory.buildOrganisationalUnits(organisationalUnits);
        organisationalUnitMapCache.put(rebuiltOrgMap);
        log.info("Cache is updated for the organisational unit and its children: {}", rebuiltOrgMap.get(organisationalUnitId));
        return rebuiltOrgMap;
    }

    private OrganisationalUnit parseParent(String parentStr, OrganisationalUnitMap map) {
        if (isBlank(parentStr)) return null;
        try {
            Long parentId = Long.parseLong(parentStr.substring(parentStr.lastIndexOf('/') + 1));
            OrganisationalUnit parent = map.get(parentId);
            if (parent == null) {
                log.error("Parent OrganisationalUnit not found for id: {}", parentId);
                throw new NotFoundException("Parent OrganisationalUnit not found for id: " + parentId);
            }
            return parent;
        } catch (NumberFormatException e) {
            log.error("Invalid parent reference: {}", parentStr);
            throw new IncorrectStateException("Invalid parent reference: " + parentStr);
        }
    }
}
