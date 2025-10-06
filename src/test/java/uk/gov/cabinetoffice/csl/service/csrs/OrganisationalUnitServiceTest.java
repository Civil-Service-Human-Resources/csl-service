package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationalUnitDto;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationalUnitsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.messaging.MessageMetadataFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class OrganisationalUnitServiceTest {

    private final OrganisationalUnitFactory organisationalUnitFactory = new OrganisationalUnitFactory();
    private OrganisationalUnitMap organisationalUnitMap;

    @Mock
    OrganisationalUnitMapCache organisationalUnitMapCache;

    @Mock
    private MessageMetadataFactory messageMetadataFactory;

    @Mock
    private IMessagingClient messagingClient;

    @Mock
    private ICSRSClient csrs;

    private OrganisationalUnitService organisationalUnitService;

    @BeforeEach
    public void setUp() {
        organisationalUnitService = new OrganisationalUnitService(organisationalUnitMapCache, csrs,
                messageMetadataFactory, messagingClient);
        organisationalUnitMap = organisationalUnitFactory.buildOrganisationalUnits(getAllOrganisationalUnits());
        when(csrs.getAllOrganisationalUnits()).thenReturn(organisationalUnitMap);
    }

    @Test
    public void shouldReturnAllOrganisationalUnits() {
        OrganisationalUnitMap organisationalUnitMap = organisationalUnitService.getOrganisationalUnitMap();
        log.debug("organisationalUnitMap: " + organisationalUnitMap);
        Set<Long> actualOrgIds = organisationalUnitMap.keySet();
        Set<Long> expectedOrgIds = Set.of(1L, 2L, 3L, 4L, 5L, 6L);
        assertEquals(expectedOrgIds, actualOrgIds);
    }

    @Test
    public void shouldReturnOrganisationsWithChildrenAsFlatList() {
        List<OrganisationalUnit> organisationsWithChildrenAsFlatList1 = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(List.of(1L));
        log.debug("organisationsWithChildrenAsFlatList for id=1: " + organisationsWithChildrenAsFlatList1);
        assertEquals(5, organisationsWithChildrenAsFlatList1.size());

        List<OrganisationalUnit> organisationsWithChildrenAsFlatList2 = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(List.of(2L));
        log.debug("organisationsWithChildrenAsFlatList for id=2: " + organisationsWithChildrenAsFlatList2);
        assertEquals(3, organisationsWithChildrenAsFlatList2.size());

        List<OrganisationalUnit> organisationsWithChildrenAsFlatList3 = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(List.of(3L));
        log.debug("organisationsWithChildrenAsFlatList for id=3: " + organisationsWithChildrenAsFlatList3);
        assertEquals(2, organisationsWithChildrenAsFlatList3.size());

        List<OrganisationalUnit> organisationsWithChildrenAsFlatList4 = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(List.of(4L));
        log.debug("organisationsWithChildrenAsFlatList for id=4: " + organisationsWithChildrenAsFlatList4);
        assertEquals(1, organisationsWithChildrenAsFlatList4.size());

        List<OrganisationalUnit> organisationsWithChildrenAsFlatList5 = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(List.of(5L));
        log.debug("organisationsWithChildrenAsFlatList for id=5: " + organisationsWithChildrenAsFlatList5);
        assertEquals(1, organisationsWithChildrenAsFlatList5.size());

        List<OrganisationalUnit> organisationsWithChildrenAsFlatList6 = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(List.of(6L));
        log.debug("organisationsWithChildrenAsFlatList for id=6: " + organisationsWithChildrenAsFlatList6);
        assertEquals(1, organisationsWithChildrenAsFlatList6.size());
    }

    @Test
    public void shouldReturnOrganisationsIdsIncludingParentAndChildren() {
        List<Long> organisationsIdsIncludingParentAndChildren1 = organisationalUnitService.getOrganisationsIdsIncludingParentAndChildren(List.of(1L));
        log.debug("organisationsWithChildrenAsFlatList for id=1: "  + organisationsIdsIncludingParentAndChildren1);
        assertEquals(5, organisationsIdsIncludingParentAndChildren1.size());

        List<Long> organisationsIdsIncludingParentAndChildren2 = organisationalUnitService.getOrganisationsIdsIncludingParentAndChildren(List.of(2L));
        log.debug("organisationsWithChildrenAsFlatList for id=2: "  + organisationsIdsIncludingParentAndChildren2);
        assertEquals(3, organisationsIdsIncludingParentAndChildren2.size());

        List<Long> organisationsIdsIncludingParentAndChildren3 = organisationalUnitService.getOrganisationsIdsIncludingParentAndChildren(List.of(3L));
        log.debug("organisationsWithChildrenAsFlatList for id=3: "  + organisationsIdsIncludingParentAndChildren3);
        assertEquals(2, organisationsIdsIncludingParentAndChildren3.size());

        List<Long> organisationsIdsIncludingParentAndChildren4 = organisationalUnitService.getOrganisationsIdsIncludingParentAndChildren(List.of(4L));
        log.debug("organisationsWithChildrenAsFlatList for id=4: "  + organisationsIdsIncludingParentAndChildren4);
        assertEquals(1, organisationsIdsIncludingParentAndChildren4.size());

        List<Long> organisationsIdsIncludingParentAndChildren5 = organisationalUnitService.getOrganisationsIdsIncludingParentAndChildren(List.of(5L));
        log.debug("organisationsWithChildrenAsFlatList for id=5: "  + organisationsIdsIncludingParentAndChildren5);
        assertEquals(1, organisationsIdsIncludingParentAndChildren5.size());

        List<Long> organisationsIdsIncludingParentAndChildren6 = organisationalUnitService.getOrganisationsIdsIncludingParentAndChildren(List.of(6L));
        log.debug("organisationsWithChildrenAsFlatList for id=6: "  + organisationsIdsIncludingParentAndChildren6);
        assertEquals(1, organisationsIdsIncludingParentAndChildren6.size());
    }

    @Test
    public void shouldReturnOrganisationsWithChildrenAsFlatListMap() {
        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap1 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(1L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=1: " + organisationsWithChildrenAsFlatListMap1);
        assertEquals(5, organisationsWithChildrenAsFlatListMap1.get(1L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap2 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(2L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=2: " + organisationsWithChildrenAsFlatListMap2);
        assertEquals(3, organisationsWithChildrenAsFlatListMap2.get(2L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap3 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(3L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=3: " + organisationsWithChildrenAsFlatListMap3);
        assertEquals(2, organisationsWithChildrenAsFlatListMap3.get(3L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap4 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(4L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=4: " + organisationsWithChildrenAsFlatListMap4);
        assertEquals(1, organisationsWithChildrenAsFlatListMap4.get(4L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap5 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(5L));
        log.debug("organisationsWithChildrenAsFlatListMap5 for id=5: " + organisationsWithChildrenAsFlatListMap5);
        assertEquals(1, organisationsWithChildrenAsFlatListMap5.get(5L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap6 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(6L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=6: " + organisationsWithChildrenAsFlatListMap6);
        assertEquals(1, organisationsWithChildrenAsFlatListMap6.get(6L).size());
    }

    @Test
    public void shouldReturnOrganisationHierarchies() {
        Map<Long, List<OrganisationalUnit>> hierarchies1 = organisationalUnitService.getHierarchies(List.of(1L));
        log.debug("hierarchies1 for id=1: " + hierarchies1);
        assertEquals(1, hierarchies1.get(1L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies2 = organisationalUnitService.getHierarchies(List.of(2L));
        log.debug("hierarchies1 for id=2: " + hierarchies2);
        assertEquals(2, hierarchies2.get(2L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies3 = organisationalUnitService.getHierarchies(List.of(3L));
        log.debug("hierarchies1 for id=3: " + hierarchies3);
        assertEquals(3, hierarchies3.get(3L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies4 = organisationalUnitService.getHierarchies(List.of(4L));
        log.debug("hierarchies1 for id=4: " + hierarchies4);
        assertEquals(4, hierarchies4.get(4L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies5 = organisationalUnitService.getHierarchies(List.of(5L));
        log.debug("hierarchies5 for id=5: " + hierarchies5);
        assertEquals(2, hierarchies5.get(5L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies6 = organisationalUnitService.getHierarchies(List.of(6L));
        log.debug("hierarchies1 for id=6: " + hierarchies6);
        assertEquals(1, hierarchies6.get(6L).size());
    }

    @Test
    public void shouldReturnFormattedOrganisationalUnitNamesForAllOrganisationsIfParametersAreNotSpecified() {
        OrganisationalUnitsParams formattedOrganisationalUnitsParams = new OrganisationalUnitsParams();
        formattedOrganisationalUnitsParams.setOrganisationId(null);
        formattedOrganisationalUnitsParams.setDomain(null);

        Map<Long, FormattedOrganisationalUnitName> orgMap = organisationalUnitService.getFormattedOrganisationalUnitNames(formattedOrganisationalUnitsParams)
                .getFormattedOrganisationalUnitNames().stream().collect(Collectors.toMap(FormattedOrganisationalUnitName::getId, Function.identity()));

        assertEquals(orgMap.size(), 6);
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

        assertEquals(orgMap.size(), 4);
        assertEquals("OrgName1 (OName1)", orgMap.get(1L).getName());
        assertEquals("OrgName1 (OName1) | OrgName2", orgMap.get(2L).getName());
        assertEquals("OrgName1 (OName1) | OrgName2 | OrgName3 (OName3)", orgMap.get(3L).getName());
        assertEquals("OrgName1 (OName1) | OrgName5 (OName5)", orgMap.get(5L).getName());
    }

    private List<OrganisationalUnit> getAllOrganisationalUnits() {

        Domain domain1 = new Domain(1L, "domain1.com", LocalDateTime.now());
        Domain domain2 = new Domain(2L, "domain2.com", LocalDateTime.now());
        Domain domain3 = new Domain(3L, "domain3.com", LocalDateTime.now());

        List<OrganisationalUnit> organisationalUnits = new ArrayList<>();

        OrganisationalUnit organisationalUnits1 = new OrganisationalUnit();
        organisationalUnits1.setId(1L);
        organisationalUnits1.setName("OrgName1");
        organisationalUnits1.setFormattedName("OrgName1 (OName1)");
        organisationalUnits1.setParentId(null);
        organisationalUnits1.setParent(null);
        organisationalUnits1.setAbbreviation("OName1");
        organisationalUnits1.setCode("ON1");
        organisationalUnits1.setDomains(Arrays.asList(domain1, domain2));
        organisationalUnits.add(organisationalUnits1);

        OrganisationalUnit organisationalUnits2 = new OrganisationalUnit();
        organisationalUnits2.setId(2L);
        organisationalUnits2.setName("OrgName2");
        organisationalUnits2.setFormattedName("OrgName1 (OName1) | OrgName2");
        organisationalUnits2.setParentId(1L);
        organisationalUnits1.setParent(organisationalUnits1);
        organisationalUnits2.setAbbreviation("");
        organisationalUnits2.setCode("ON2");
        organisationalUnits2.setDomains(Arrays.asList(domain1, domain2, domain3));
        organisationalUnits.add(organisationalUnits2);

        OrganisationalUnit organisationalUnits3 = new OrganisationalUnit();
        organisationalUnits3.setId(3L);
        organisationalUnits3.setName("OrgName3");
        organisationalUnits3.setFormattedName("OrgName1 (OName1) | OrgName2 | OrgName3 (OName3)");
        organisationalUnits3.setParentId(2L);
        organisationalUnits1.setParent(organisationalUnits2);
        organisationalUnits3.setAbbreviation("OName3");
        organisationalUnits3.setCode("ON3");
        organisationalUnits.add(organisationalUnits3);

        OrganisationalUnit organisationalUnits4 = new OrganisationalUnit();
        organisationalUnits4.setId(4L);
        organisationalUnits4.setName("OrgName4");
        organisationalUnits4.setFormattedName("OrgName1 (OName1) | OrgName2 | OrgName3 (OName3) | OrgName4 (OName4)");
        organisationalUnits4.setParentId(3L);
        organisationalUnits1.setParent(organisationalUnits3);
        organisationalUnits4.setAbbreviation("OName4");
        organisationalUnits4.setCode("ON4");
        organisationalUnits.add(organisationalUnits4);

        OrganisationalUnit organisationalUnits5 = new OrganisationalUnit();
        organisationalUnits5.setId(5L);
        organisationalUnits5.setName("OrgName5");
        organisationalUnits5.setFormattedName("OrgName1 (OName1) | OrgName5 (OName5)");
        organisationalUnits5.setParentId(1L);
        organisationalUnits1.setParent(organisationalUnits1);
        organisationalUnits5.setAbbreviation("OName5");
        organisationalUnits5.setCode("ON5");
        organisationalUnits5.setDomains(Arrays.asList(domain1, domain2, domain3));
        organisationalUnits.add(organisationalUnits5);

        OrganisationalUnit organisationalUnits6 = new OrganisationalUnit();
        organisationalUnits6.setId(6L);
        organisationalUnits6.setName("OrgName6");
        organisationalUnits6.setFormattedName("OrgName6 (OName6)");
        organisationalUnits6.setAbbreviation("OName6");
        organisationalUnits6.setCode("ON6");
        organisationalUnits6.setDomains(List.of(domain3));
        organisationalUnits.add(organisationalUnits6);

        return organisationalUnits;
    }

    @Test
    void scenario1_removeGrandParentFromParent() {
        OrganisationalUnitDto dto = new OrganisationalUnitDto();
        dto.setName("Parent Org");
        dto.setAbbreviation("PO");
        dto.setCode("PO-CODE");
        dto.setParent(null);

        OrganisationalUnit originalOrganisationalUnit = organisationalUnitMap.get(2L);
        Long originalParentId = originalOrganisationalUnit.getParentId();
        OrganisationalUnit originalParent = organisationalUnitMap.get(originalParentId);
        assertTrue(originalParent.getChildIds().contains(2L));

        OrganisationalUnitMap updateOrganisationalUnitMap = organisationalUnitService.updateOrganisationalUnitsInCache(2L, dto);
        OrganisationalUnit updatedOrganisationalUnit = updateOrganisationalUnitMap.get(2L);

        //Updated formatted name
        assertEquals("Parent Org (PO)", updatedOrganisationalUnit.getFormattedName());

        // parent should be top-level
        assertNull(updatedOrganisationalUnit.getParentId());
        assertNull(updatedOrganisationalUnit.getParent());

        // child IDs
        Set<Long> childIds = updatedOrganisationalUnit.getChildIds();
        assertTrue(childIds.contains(3L), "Parent should still have Child as child");
        OrganisationalUnit childOrganisationalUnit = updateOrganisationalUnitMap.get(3L);
        assertEquals("Parent Org (PO) | OrgName3 (OName3)", childOrganisationalUnit.getFormattedName());

        // original parent should not track update child
        OrganisationalUnit updatedParent = updateOrganisationalUnitMap.get(originalParentId);
        assertFalse(updatedParent.getChildIds().contains(2L), "Grand Parent should no longer have Parent as child");
    }

    @Test
    void scenario2_removeParentFromChild() {
        OrganisationalUnitDto dto = new OrganisationalUnitDto();
        dto.setName("Child Org");
        dto.setAbbreviation("CO");
        dto.setCode("CO-CODE");
        dto.setParent(null);

        OrganisationalUnit originalOrganisationalUnit = organisationalUnitMap.get(3L);
        Long originalParentId = originalOrganisationalUnit.getParentId();
        OrganisationalUnit originalParent = organisationalUnitMap.get(originalParentId);
        assertTrue(originalParent.getChildIds().contains(3L));

        OrganisationalUnitMap updateOrganisationalUnitMap = organisationalUnitService.updateOrganisationalUnitsInCache(3L, dto);
        OrganisationalUnit updatedOrganisationalUnit = updateOrganisationalUnitMap.get(3L);

        // formatted names
        assertEquals("Child Org (CO)", updatedOrganisationalUnit.getFormattedName());

        // child should be top-level
        assertNull(updatedOrganisationalUnit.getParentId());
        assertNull(updatedOrganisationalUnit.getParent());

        // child IDs
        Set<Long> childIds = updatedOrganisationalUnit.getChildIds();
        assertTrue(childIds.contains(4L), "Child should still have further Child as child");
        OrganisationalUnit childOrganisationalUnit = updateOrganisationalUnitMap.get(4L);
        assertEquals("Child Org (CO) | OrgName4 (OName4)", childOrganisationalUnit.getFormattedName());

        // original parent should not track update child
        OrganisationalUnit updatedParent = updateOrganisationalUnitMap.get(originalParentId);
        assertFalse(updatedParent.getChildIds().contains(3L), "Grand Parent should no longer have Parent as child");
    }

    @Test
    void scenario3_makeGrandParentParentOfChild() {
        OrganisationalUnitDto dto = new OrganisationalUnitDto();
        dto.setName("Child Org");
        dto.setAbbreviation("CO");
        dto.setCode("CO-CODE");
        dto.setParent("/organisationalUnits/1"); // grandparent

        OrganisationalUnit originalOrganisationalUnit = organisationalUnitMap.get(3L);
        Long originalParentId = originalOrganisationalUnit.getParentId();
        assertEquals(originalParentId, 2L);
        OrganisationalUnit originalParent = organisationalUnitMap.get(originalParentId);
        assertTrue(originalParent.getChildIds().contains(3L));

        OrganisationalUnitMap updateOrganisationalUnitMap = organisationalUnitService.updateOrganisationalUnitsInCache(3L, dto);
        OrganisationalUnit updatedOrganisationalUnit = updateOrganisationalUnitMap.get(3L);

        // formatted names
        assertEquals("OrgName1 (OName1) | Child Org (CO)", updatedOrganisationalUnit.getFormattedName());

        // new parent
        assertEquals(updatedOrganisationalUnit.getParentId(), 1L);

        // child IDs
        Set<Long> childIds = updatedOrganisationalUnit.getChildIds();
        assertTrue(childIds.contains(4L), "Child should still have further Child as child");
        OrganisationalUnit childOrganisationalUnit = updateOrganisationalUnitMap.get(4L);
        assertEquals("OrgName1 (OName1) | Child Org (CO) | OrgName4 (OName4)", childOrganisationalUnit.getFormattedName());

        // original parent should not track update child
        OrganisationalUnit updatedParent = updateOrganisationalUnitMap.get(originalParentId);
        assertFalse(updatedParent.getChildIds().contains(3L), "Grand Parent should no longer have Parent as child");
    }
}
