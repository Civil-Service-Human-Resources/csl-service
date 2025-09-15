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
        log.info("Updating organisational unit for id: {}", organisationalUnitId);
        civilServantRegistryClient.patchOrganisationalUnit(organisationalUnitId, organisationalUnitDto);
        OrganisationalUnit organisationalUnit = updateOrganisationalUnitsInCache(organisationalUnitId, organisationalUnitDto);
        updateReportingData(organisationalUnitId, organisationalUnit.getFormattedName());
    }

    private void updateReportingData(Long organisationalUnitId, String formattedOrganisationName) {
        log.debug("updatingReportingData: {} for organisationalUnitId: {}", formattedOrganisationName, organisationalUnitId);
        RegisteredLearnerOrganisationUpdateMessage message = messageMetadataFactory.generateRegisteredLearnersOrganisationUpdateMessage(organisationalUnitId, formattedOrganisationName);
        messagingClient.sendMessages(List.of(message));
    }

    public void removeAllOrganisationalUnitsFromCache() {
        organisationalUnitMapCache.evict();
        log.info("Organisations are removed from the cache.");
    }

    public void removeOrganisationalUnitsFromCache(List<Long> organisationIds) {
        log.info("Removing organisationalUnit and its children FromCache for the organisationalUnitIds: {}", organisationIds);
        organisationIds.forEach(organisationalUnitId -> getOrganisationalUnitMap().remove(organisationalUnitId));
    }

    public void removeOrganisationalUnitAndChildrenFromCache(Long organisationalUnitId) {
        removeOrganisationalUnitsFromCache(getOrganisationsIdsIncludingParentAndChildren(List.of(organisationalUnitId)));
    }

    public OrganisationalUnit updateOrganisationalUnitsInCache(Long organisationalUnitId, OrganisationalUnitDto organisationalUnitDto) {
        OrganisationalUnitMap organisationalUnitMap = getOrganisationalUnitMap();
        OrganisationalUnit organisationalUnit = organisationalUnitMap.get(organisationalUnitId);
        if (organisationalUnit == null) {
            log.error("OrganisationalUnit not found for id: {}", organisationalUnitId);
            throw new NotFoundException("OrganisationalUnit not found for id: " + organisationalUnitId);
        }

        organisationalUnit.setAbbreviation(organisationalUnitDto.getAbbreviation());
        organisationalUnit.setCode(organisationalUnitDto.getCode());
        organisationalUnit.setName(organisationalUnitDto.getName());

        Long newParentId = null;
        OrganisationalUnit newParentOrganisationalUnit = null;
        String newParent = organisationalUnitDto.getParent();

        if (isNotBlank(newParent)) {
            try {
                newParentId = Long.parseLong(newParent.substring(newParent.lastIndexOf('/') + 1));
                newParentOrganisationalUnit = organisationalUnitMap.get(newParentId);
                if(newParentOrganisationalUnit == null) {
                    log.error("Parent OrganisationalUnit not found for id: {}", newParentId);
                    throw new NotFoundException("Parent OrganisationalUnit not found for id: " + newParentId);
                }
            } catch (NumberFormatException e) {
                log.error("Invalid parent reference: {}", newParent);
                throw new IncorrectStateException("Invalid parent reference: " + newParent);
            }
        }
        organisationalUnit.setParentId(newParentId);
        organisationalUnit.setParent(newParentOrganisationalUnit);
        organisationalUnitMap.replace(organisationalUnitId, organisationalUnit);
        buildOrganisationalUnits(organisationalUnitMap);
        return organisationalUnit;
    }

    private void buildOrganisationalUnits(OrganisationalUnitMap organisationalUnitMap) {
        List<OrganisationalUnit> organisationalUnits = new ArrayList<>(organisationalUnitMap.values());

        organisationalUnits.forEach(o -> {
            StringBuilder formattedName = new StringBuilder(o.getNameWithAbbreviation());
            Long parentId = o.getParentId();
            int parents = 0;

            while (parentId != null) {
                OrganisationalUnit parentOrganisationalUnit = organisationalUnitMap.get(parentId);
                if (parents == 0) {
                    parentOrganisationalUnit.addChildId(o.getId());
                    parents++;
                }
                if (parentOrganisationalUnit.getAgencyToken() != null
                        && o.getAgencyTokenOrInherited().isEmpty()) {
                    o.setInheritedAgencyToken(parentOrganisationalUnit.getAgencyToken());
                }
                formattedName.insert(0, parentOrganisationalUnit.getNameWithAbbreviation() + " | ");
                parentId = parentOrganisationalUnit.getParentId();
            }

            o.setFormattedName(formattedName.toString());
            organisationalUnitMap.replace(o.getId(), o);
        });
    }
}
