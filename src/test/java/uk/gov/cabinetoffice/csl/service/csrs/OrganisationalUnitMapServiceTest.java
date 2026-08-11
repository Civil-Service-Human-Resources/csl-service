package uk.gov.cabinetoffice.csl.service.csrs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitDto;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitOverview;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnitFactory;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnitMap;
import uk.gov.cabinetoffice.csl.domain.taxonomy.BasicTaxonomyNode;
import uk.gov.cabinetoffice.csl.service.ITaxonomyMapCacheClient;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganisationalUnitMapServiceTest extends CsrsServiceTestBase {

    private OrganisationalUnitMap organisationalUnitMap;

    @Mock
    private OrganisationalUnitFactory organisationalUnitFactory;

    @Mock
    private Cache cache;

    @Mock
    private IMessagingClient messagingClient;

    @Mock
    private ITaxonomyMapCacheClient<OrganisationalUnit, BasicTaxonomyNode,
            OrganisationalUnitMap, OrganisationalUnitDto> client;

    @InjectMocks
    private OrganisationalUnitMapService service;

    @BeforeEach
    public void setUp() {
        organisationalUnitMap = OrganisationalUnitMap.buildFromList(getAllOrganisationalUnits());
        when(client.fetch()).thenReturn(organisationalUnitMap);
    }

    @Test
    void testRemoveGrandparent() {
        OrganisationalUnitDto dto = new OrganisationalUnitDto();
        dto.setName("Parent Org");
        dto.setAbbreviation("PO");
        dto.setCode("PO-CODE");
        dto.setParentId(null);

        OrganisationalUnit originalOrganisationalUnit = organisationalUnitMap.get(2L);
        Long originalParentId = originalOrganisationalUnit.getParentId();
        OrganisationalUnit originalParent = organisationalUnitMap.get(originalParentId);
        assertTrue(originalParent.getChildIds().contains(2L));

        when(organisationalUnitFactory.createOverview(originalOrganisationalUnit)).thenReturn(new OrganisationalUnitOverview());

        service.update(2L, dto);
        OrganisationalUnit updatedOrganisationalUnit = organisationalUnitMap.get(2L);

        //Updated formatted name
        assertEquals("Parent Org (PO)", updatedOrganisationalUnit.getFormattedName());
        assertEquals("Parent Org", updatedOrganisationalUnit.getFormattedNameWithoutAbbreviation());
        assertEquals("Parent Org", updatedOrganisationalUnit.getName());
        assertEquals("PO", updatedOrganisationalUnit.getAbbreviation());
        assertEquals("PO-CODE", updatedOrganisationalUnit.getCode());

        // parent should be top-level
        assertNull(updatedOrganisationalUnit.getParentId());
        assertNull(updatedOrganisationalUnit.getParent());

        // child IDs
        Set<Long> childIds = updatedOrganisationalUnit.getChildIds();
        assertTrue(childIds.contains(3L), "Parent should still have Child as child");
        OrganisationalUnit childOrganisationalUnit = organisationalUnitMap.get(3L);
        assertEquals("Parent Org (PO) | OrgName3 (OName3)", childOrganisationalUnit.getFormattedName());
        assertEquals("Parent Org | OrgName3", childOrganisationalUnit.getFormattedNameWithoutAbbreviation());

        // original parent should not track update child
        OrganisationalUnit updatedOriginalParent = organisationalUnitMap.get(originalParentId);
        assertFalse(updatedOriginalParent.getChildIds().contains(2L), "Grand Parent should no longer have Parent as child");
    }

    @Test
    void testRemoveParent() {
        OrganisationalUnitDto dto = new OrganisationalUnitDto();
        dto.setName("Child Org");
        dto.setAbbreviation("CO");
        dto.setCode("CO-CODE");
        dto.setParentId(null);

        OrganisationalUnit originalOrganisationalUnit = organisationalUnitMap.get(3L);
        Long originalParentId = originalOrganisationalUnit.getParentId();
        OrganisationalUnit originalParent = organisationalUnitMap.get(originalParentId);
        assertTrue(originalParent.getChildIds().contains(3L));

        service.update(3L, dto);
        OrganisationalUnit updatedOrganisationalUnit = organisationalUnitMap.get(3L);

        // formatted names
        assertEquals("Child Org (CO)", updatedOrganisationalUnit.getFormattedName());
        assertEquals("Child Org", updatedOrganisationalUnit.getFormattedNameWithoutAbbreviation());
        assertEquals("Child Org", updatedOrganisationalUnit.getName());
        assertEquals("CO", updatedOrganisationalUnit.getAbbreviation());
        assertEquals("CO-CODE", updatedOrganisationalUnit.getCode());

        // child should be top-level
        assertNull(updatedOrganisationalUnit.getParentId());
        assertNull(updatedOrganisationalUnit.getParent());

        // child IDs
        Set<Long> childIds = updatedOrganisationalUnit.getChildIds();
        assertTrue(childIds.contains(4L), "Child should still have further Child as child");
        OrganisationalUnit childOrganisationalUnit = organisationalUnitMap.get(4L);
        assertEquals("Child Org (CO) | OrgName4 (OName4)", childOrganisationalUnit.getFormattedName());
        assertEquals("Child Org | OrgName4", childOrganisationalUnit.getFormattedNameWithoutAbbreviation());

        // original parent should not track update child
        OrganisationalUnit updatedOriginalParent = organisationalUnitMap.get(originalParentId);
        assertFalse(updatedOriginalParent.getChildIds().contains(3L), "Grand Parent should no longer have Parent as child");
    }

    @Test
    public void makeGrandParentParentOfChild() {
        OrganisationalUnitDto dto = new OrganisationalUnitDto();
        dto.setName("Child Org");
        dto.setAbbreviation("CO");
        dto.setCode("CO-CODE");
        dto.setParentId(1L); // grandparent

        OrganisationalUnit originalOrganisationalUnit = organisationalUnitMap.get(3L);
        Long originalParentId = originalOrganisationalUnit.getParentId();
        assertEquals(2L, originalParentId);
        OrganisationalUnit originalParent = organisationalUnitMap.get(originalParentId);
        assertTrue(originalParent.getChildIds().contains(3L));

        service.update(3L, dto);
        OrganisationalUnit updatedOrganisationalUnit = organisationalUnitMap.get(3L);
        // formatted names
        assertEquals("OrgName1 (OName1) | Child Org (CO)", updatedOrganisationalUnit.getFormattedName());
        assertEquals("OrgName1 | Child Org", updatedOrganisationalUnit.getFormattedNameWithoutAbbreviation());
        assertEquals("Child Org", updatedOrganisationalUnit.getName());
        assertEquals("CO", updatedOrganisationalUnit.getAbbreviation());
        assertEquals("CO-CODE", updatedOrganisationalUnit.getCode());

        // new parent
        assertEquals(1L, updatedOrganisationalUnit.getParentId());

        // child IDs
        Set<Long> childIds = updatedOrganisationalUnit.getChildIds();
        assertTrue(childIds.contains(4L), "Child should still have further Child as child");
        OrganisationalUnit childOrganisationalUnit = organisationalUnitMap.get(4L);
        assertEquals("OrgName1 (OName1) | Child Org (CO) | OrgName4 (OName4)", childOrganisationalUnit.getFormattedName());
        assertEquals("OrgName1 | Child Org | OrgName4", childOrganisationalUnit.getFormattedNameWithoutAbbreviation());

        // original parent should not track update child
        OrganisationalUnit updatedOriginalParent = organisationalUnitMap.get(originalParentId);
        assertFalse(updatedOriginalParent.getChildIds().contains(3L), "Grand Parent should no longer have Parent as child");
    }

}
