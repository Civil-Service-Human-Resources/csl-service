package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitDto;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitOverview;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnitMap;
import uk.gov.cabinetoffice.csl.domain.taxonomy.BasicTaxonomyNode;
import uk.gov.cabinetoffice.csl.service.CachedTaxonomyMapService;
import uk.gov.cabinetoffice.csl.service.ITaxonomyItemFactory;
import uk.gov.cabinetoffice.csl.service.ITaxonomyMapCacheClient;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.messaging.MessageMetadataFactory;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.RegisteredLearnerOrganisationUpdateMessage;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class OrganisationalUnitMapService extends CachedTaxonomyMapService<OrganisationalUnit, BasicTaxonomyNode, OrganisationalUnitMap, OrganisationalUnitDto, OrganisationalUnitOverview> {

    private final MessageMetadataFactory messageMetadataFactory;
    private final IMessagingClient messagingClient;

    public OrganisationalUnitMapService(@Qualifier("organisations") Cache cache, ITaxonomyItemFactory<OrganisationalUnit,
            OrganisationalUnitOverview> taxonomyItemFactory, ITaxonomyMapCacheClient<OrganisationalUnit, BasicTaxonomyNode,
            OrganisationalUnitMap, OrganisationalUnitDto> client, MessageMetadataFactory messageMetadataFactory, IMessagingClient messagingClient) {
        super(cache, "organisationalUnitMap", OrganisationalUnitMap.class, taxonomyItemFactory, client);
        this.messageMetadataFactory = messageMetadataFactory;
        this.messagingClient = messagingClient;
    }

    @Override
    public OrganisationalUnitOverview update(Long id, OrganisationalUnitDto dto) {
        OrganisationalUnitMap map = get();
        OrganisationalUnit organisationalUnit = map.get(id);
        map.validateUpdate(id, dto.getParentId());
        client.patch(id, dto);
        if (!Objects.equals(organisationalUnit.getParentId(), dto.getParentId())) {
            organisationalUnit = map.updateParent(organisationalUnit, dto.getParentId());
        }
        updateObjectWithDto(organisationalUnit, dto);
        List<OrganisationalUnit> multipleOrgs = map.rebuildHierarchy(organisationalUnit);
        if (!organisationalUnit.getName().equals(dto.getName())) {
            RegisteredLearnerOrganisationUpdateMessage message = messageMetadataFactory.generateRegisteredLearnersOrganisationUpdateMessage(multipleOrgs);
            log.info("Sending organisational unit message to update reporting data: {}", message);
            messagingClient.sendMessages(List.of(message));
        }
        put(map);
        return taxonomyItemFactory.createOverview(organisationalUnit);
    }

    protected void updateObjectWithDto(OrganisationalUnit object, OrganisationalUnitDto dto) {
        super.updateObjectWithDto(object, dto);
        object.setAbbreviation(dto.getAbbreviation());
    }
}
