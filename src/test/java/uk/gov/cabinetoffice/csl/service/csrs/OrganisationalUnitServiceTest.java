package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitDto;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitOverview;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.FormattedOrganisationalUnitName;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnitFactory;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnitMap;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.messaging.MessageMetadataFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class OrganisationalUnitServiceTest extends CsrsServiceTestBase {

    private OrganisationalUnitMap organisationalUnitMap;

    @Mock
    private OrganisationalUnitFactory organisationalUnitFactory;

    @Mock
    OrganisationalUnitMapCache organisationalUnitMapCache;

    @Mock
    private IMessagingClient messagingClient;

    @Mock
    private ICSRSClient csrs;

    @InjectMocks
    private OrganisationalUnitService organisationalUnitService;

    @BeforeEach
    public void setUp() {
        organisationalUnitMap = OrganisationalUnitMap.buildFromList(getAllOrganisationalUnits());
        when(organisationalUnitMapCache.get()).thenReturn(organisationalUnitMap);
        organisationalUnitService = new OrganisationalUnitService(organisationalUnitMapCache, organisationalUnitFactory, csrs,
                new MessageMetadataFactory(), messagingClient);
    }

    @Test
    public void shouldReturnAllOrganisationalUnits() {
        OrganisationalUnitMap organisationalUnitMap = organisationalUnitService.getOrganisationalUnitMap();
        log.debug("organisationalUnitMap: {} ", organisationalUnitMap);
        Set<Long> actualOrgIds = organisationalUnitMap.keySet();
        Set<Long> expectedOrgIds = Set.of(1L, 2L, 3L, 4L, 5L, 6L);
        assertEquals(expectedOrgIds, actualOrgIds);
    }

    @Test
    public void shouldReturnOrganisationsWithChildrenAsFlatList() {
        List<Long> organisationsWithChildrenAsFlatList1 = organisationalUnitService.getOrganisationIdsWithChildrenAsFlatList(List.of(1L));
        log.debug("organisationsWithChildrenAsFlatList for id=1: {}", organisationsWithChildrenAsFlatList1);
        assertEquals(5, organisationsWithChildrenAsFlatList1.size());

        List<Long> organisationsWithChildrenAsFlatList2 = organisationalUnitService.getOrganisationIdsWithChildrenAsFlatList(List.of(2L));
        log.debug("organisationsWithChildrenAsFlatList for id=2: {}", organisationsWithChildrenAsFlatList2);
        assertEquals(3, organisationsWithChildrenAsFlatList2.size());

        List<Long> organisationsWithChildrenAsFlatList3 = organisationalUnitService.getOrganisationIdsWithChildrenAsFlatList(List.of(3L));
        log.debug("organisationsWithChildrenAsFlatList for id=3: {}", organisationsWithChildrenAsFlatList3);
        assertEquals(2, organisationsWithChildrenAsFlatList3.size());

        List<Long> organisationsWithChildrenAsFlatList4 = organisationalUnitService.getOrganisationIdsWithChildrenAsFlatList(List.of(4L));
        log.debug("organisationsWithChildrenAsFlatList for id=4: {}", organisationsWithChildrenAsFlatList4);
        assertEquals(1, organisationsWithChildrenAsFlatList4.size());

        List<Long> organisationsWithChildrenAsFlatList5 = organisationalUnitService.getOrganisationIdsWithChildrenAsFlatList(List.of(5L));
        log.debug("organisationsWithChildrenAsFlatList for id=5: {}", organisationsWithChildrenAsFlatList5);
        assertEquals(1, organisationsWithChildrenAsFlatList5.size());

        List<Long> organisationsWithChildrenAsFlatList6 = organisationalUnitService.getOrganisationIdsWithChildrenAsFlatList(List.of(6L));
        log.debug("organisationsWithChildrenAsFlatList for id=6: {}", organisationsWithChildrenAsFlatList6);
        assertEquals(1, organisationsWithChildrenAsFlatList6.size());
    }

    @Test
    public void shouldReturnOrganisationsWithChildrenAsFlatListMap() {
        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap1 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(1L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=1: {}", organisationsWithChildrenAsFlatListMap1);
        assertEquals(5, organisationsWithChildrenAsFlatListMap1.get(1L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap2 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(2L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=2: {}", organisationsWithChildrenAsFlatListMap2);
        assertEquals(3, organisationsWithChildrenAsFlatListMap2.get(2L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap3 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(3L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=3: {}", organisationsWithChildrenAsFlatListMap3);
        assertEquals(2, organisationsWithChildrenAsFlatListMap3.get(3L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap4 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(4L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=4: {}", organisationsWithChildrenAsFlatListMap4);
        assertEquals(1, organisationsWithChildrenAsFlatListMap4.get(4L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap5 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(5L));
        log.debug("organisationsWithChildrenAsFlatListMap5 for id=5: {}", organisationsWithChildrenAsFlatListMap5);
        assertEquals(1, organisationsWithChildrenAsFlatListMap5.get(5L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap6 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(6L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=6: {}", organisationsWithChildrenAsFlatListMap6);
        assertEquals(1, organisationsWithChildrenAsFlatListMap6.get(6L).size());
    }

    @Test
    public void shouldReturnOrganisationHierarchies() {
        Map<Long, List<OrganisationalUnit>> hierarchies1 = organisationalUnitService.getHierarchies(List.of(1L));
        log.debug("hierarchies1 for id=1: {}", hierarchies1);
        assertEquals(1, hierarchies1.get(1L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies2 = organisationalUnitService.getHierarchies(List.of(2L));
        log.debug("hierarchies1 for id=2: {}", hierarchies2);
        assertEquals(2, hierarchies2.get(2L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies3 = organisationalUnitService.getHierarchies(List.of(3L));
        log.debug("hierarchies1 for id=3: {}", hierarchies3);
        assertEquals(3, hierarchies3.get(3L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies4 = organisationalUnitService.getHierarchies(List.of(4L));
        log.debug("hierarchies1 for id=4: {}", hierarchies4);
        assertEquals(4, hierarchies4.get(4L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies5 = organisationalUnitService.getHierarchies(List.of(5L));
        log.debug("hierarchies5 for id=5: {}", hierarchies5);
        assertEquals(2, hierarchies5.get(5L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies6 = organisationalUnitService.getHierarchies(List.of(6L));
        log.debug("hierarchies1 for id=6: {}", hierarchies6);
        assertEquals(1, hierarchies6.get(6L).size());
    }

    @Test
    public void shouldReturnFormattedOrganisationalUnitNamesForAllOrganisationsIfParametersAreNotSpecified() {
        OrganisationalUnitsParams formattedOrganisationalUnitsParams = new OrganisationalUnitsParams();
        formattedOrganisationalUnitsParams.setOrganisationId(null);
        formattedOrganisationalUnitsParams.setDomain(null);

        Map<Long, FormattedOrganisationalUnitName> orgMap = organisationalUnitService.getFormattedOrganisationalUnitNames(formattedOrganisationalUnitsParams)
                .getFormattedOrganisationalUnitNames().stream().collect(Collectors.toMap(FormattedOrganisationalUnitName::getId, Function.identity()));

        assertEquals(6, orgMap.size());
        assertEquals("OrgName1 (OName1)", orgMap.get(1L).getName());
        assertEquals("OrgName1 (OName1) | OrgName2", orgMap.get(2L).getName());
        assertEquals("OrgName1 (OName1) | OrgName2 | OrgName3 (OName3)", orgMap.get(3L).getName());
        assertEquals("OrgName1 (OName1) | OrgName2 | OrgName3 (OName3) | OrgName4 (OName4)", orgMap.get(4L).getName());
        assertEquals("OrgName1 (OName1) | OrgName5 (OName5)", orgMap.get(5L).getName());
        assertEquals("OrgName6 (OName6)", orgMap.get(6L).getName());
    }

    @Test
    public void shouldReturnFormattedOrganisationalUnitNamesForFilteredOrganisationsIfParametersAreSpecified() {
        OrganisationalUnitsParams formattedOrganisationalUnitsParams = new OrganisationalUnitsParams();
        formattedOrganisationalUnitsParams.setOrganisationId(Arrays.asList(1L, 2L, 3L));
        formattedOrganisationalUnitsParams.setDomain("domain1.com");

        Map<Long, FormattedOrganisationalUnitName> orgMap = organisationalUnitService.getFormattedOrganisationalUnitNames(formattedOrganisationalUnitsParams)
                .getFormattedOrganisationalUnitNames().stream().collect(Collectors.toMap(FormattedOrganisationalUnitName::getId, Function.identity()));

        assertEquals(4, orgMap.size());
        assertEquals("OrgName1 (OName1)", orgMap.get(1L).getName());
        assertEquals("OrgName1 (OName1) | OrgName2", orgMap.get(2L).getName());
        assertEquals("OrgName1 (OName1) | OrgName2 | OrgName3 (OName3)", orgMap.get(3L).getName());
        assertEquals("OrgName1 (OName1) | OrgName5 (OName5)", orgMap.get(5L).getName());
    }

    @Test
    void scenario1_removeGrandParentFromParent() {
        OrganisationalUnitDto dto = new OrganisationalUnitDto();
        dto.setName("Parent Org");
        dto.setAbbreviation("PO");
        dto.setCode("PO-CODE");
        dto.setParentId(null);

        OrganisationalUnit originalOrganisationalUnit = organisationalUnitMap.get(2L);
        Long originalParentId = originalOrganisationalUnit.getParentId();
        OrganisationalUnit originalParent = organisationalUnitMap.get(originalParentId);
        assertTrue(originalParent.getChildIds().contains(2L));

        when(organisationalUnitFactory.createOrganisationalUnitOverview(originalOrganisationalUnit)).thenReturn(new OrganisationalUnitOverview());

        organisationalUnitService.patchOrganisationalUnit(2L, dto);
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
    void scenario2_removeParentFromChild() {
        OrganisationalUnitDto dto = new OrganisationalUnitDto();
        dto.setName("Child Org");
        dto.setAbbreviation("CO");
        dto.setCode("CO-CODE");
        dto.setParentId(null);

        OrganisationalUnit originalOrganisationalUnit = organisationalUnitMap.get(3L);
        Long originalParentId = originalOrganisationalUnit.getParentId();
        OrganisationalUnit originalParent = organisationalUnitMap.get(originalParentId);
        assertTrue(originalParent.getChildIds().contains(3L));

        organisationalUnitService.patchOrganisationalUnit(3L, dto);
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
    void scenario3_makeGrandParentParentOfChild() {
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

        organisationalUnitService.patchOrganisationalUnit(3L, dto);
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

    @Test
    public void shouldRemoveParentAndChildrenOrganisationalUnits() {
        assertEquals(6, organisationalUnitMap.size());
        assertTrue(organisationalUnitMap.containsKey(1L));
        organisationalUnitService.removeOrganisationalUnitsFromCache(List.of(1L));
        assertEquals(1, organisationalUnitMap.size());
        assertFalse(organisationalUnitMap.containsKey(1L));
        assertFalse(organisationalUnitMap.containsKey(2L));
        assertFalse(organisationalUnitMap.containsKey(3L));
        assertFalse(organisationalUnitMap.containsKey(4L));
        assertFalse(organisationalUnitMap.containsKey(5L));
        assertTrue(organisationalUnitMap.containsKey(6L));
    }
}
