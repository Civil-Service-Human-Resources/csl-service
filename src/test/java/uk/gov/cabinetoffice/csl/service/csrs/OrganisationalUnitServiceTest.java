package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationalUnitsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class OrganisationalUnitServiceTest {

    private final OrganisationalUnitFactory organisationalUnitFactory = new OrganisationalUnitFactory();

    @Mock
    OrganisationalUnitMapCache organisationalUnitMapCache;

    @Mock
    private ICSRSClient csrs;

    @InjectMocks
    private OrganisationalUnitService organisationalUnitService;

    @BeforeEach
    public void setUp() {
        OrganisationalUnitMap map = organisationalUnitFactory.buildOrganisationalUnits(getAllOrganisationalUnits());
        when(csrs.getAllOrganisationalUnits()).thenReturn(map);
    }

    @Test
    public void shouldReturnAllOrganisationalUnits() {
        OrganisationalUnitMap organisationalUnitMap = organisationalUnitService.getOrganisationalUnitMap();
        log.debug("organisationalUnitMap:"  + organisationalUnitMap);
        Set<Long> actualOrgIds = organisationalUnitMap.keySet();
        Set<Long> expectedOrgIds = Set.of(1L, 2L, 3L, 4L, 5L, 6L);
        assertEquals(expectedOrgIds, actualOrgIds);
    }

    @Test
    public void shouldReturnOrganisationsWithChildrenAsFlatList() {
        List<OrganisationalUnit> organisationsWithChildrenAsFlatList1 = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(List.of(1L));
        log.debug("organisationsWithChildrenAsFlatList for id=1:"  + organisationsWithChildrenAsFlatList1);
        assertEquals(5, organisationsWithChildrenAsFlatList1.size());

        List<OrganisationalUnit> organisationsWithChildrenAsFlatList2 = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(List.of(2L));
        log.debug("organisationsWithChildrenAsFlatList for id=2:"  + organisationsWithChildrenAsFlatList2);
        assertEquals(3, organisationsWithChildrenAsFlatList2.size());

        List<OrganisationalUnit> organisationsWithChildrenAsFlatList3 = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(List.of(3L));
        log.debug("organisationsWithChildrenAsFlatList for id=3:"  + organisationsWithChildrenAsFlatList3);
        assertEquals(2, organisationsWithChildrenAsFlatList3.size());

        List<OrganisationalUnit> organisationsWithChildrenAsFlatList4 = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(List.of(4L));
        log.debug("organisationsWithChildrenAsFlatList for id=4:"  + organisationsWithChildrenAsFlatList4);
        assertEquals(1, organisationsWithChildrenAsFlatList4.size());

        List<OrganisationalUnit> organisationsWithChildrenAsFlatList5 = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(List.of(5L));
        log.debug("organisationsWithChildrenAsFlatList for id=5:"  + organisationsWithChildrenAsFlatList5);
        assertEquals(1, organisationsWithChildrenAsFlatList5.size());

        List<OrganisationalUnit> organisationsWithChildrenAsFlatList6 = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(List.of(6L));
        log.debug("organisationsWithChildrenAsFlatList for id=6:"  + organisationsWithChildrenAsFlatList6);
        assertEquals(1, organisationsWithChildrenAsFlatList6.size());
    }

    @Test
    public void shouldReturnOrganisationsWithChildrenAsFlatListMap() {
        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap1 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(1L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=1:"  + organisationsWithChildrenAsFlatListMap1);
        assertEquals(5, organisationsWithChildrenAsFlatListMap1.get(1L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap2 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(2L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=2:"  + organisationsWithChildrenAsFlatListMap2);
        assertEquals(3, organisationsWithChildrenAsFlatListMap2.get(2L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap3 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(3L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=3:"  + organisationsWithChildrenAsFlatListMap3);
        assertEquals(2, organisationsWithChildrenAsFlatListMap3.get(3L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap4 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(4L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=4:"  + organisationsWithChildrenAsFlatListMap4);
        assertEquals(1, organisationsWithChildrenAsFlatListMap4.get(4L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap5 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(5L));
        log.debug("organisationsWithChildrenAsFlatListMap5 for id=5:"  + organisationsWithChildrenAsFlatListMap5);
        assertEquals(1, organisationsWithChildrenAsFlatListMap5.get(5L).size());

        Map<Long, List<OrganisationalUnit>> organisationsWithChildrenAsFlatListMap6 = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(List.of(6L));
        log.debug("organisationsWithChildrenAsFlatListMap1 for id=6:"  + organisationsWithChildrenAsFlatListMap6);
        assertEquals(1, organisationsWithChildrenAsFlatListMap6.get(6L).size());
    }

    @Test
    public void shouldReturnHierarchies() {
        Map<Long, List<OrganisationalUnit>> hierarchies1 = organisationalUnitService.getHierarchies(List.of(1L));
        log.debug("hierarchies1 for id=1:"  + hierarchies1);
        assertEquals(1, hierarchies1.get(1L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies2 = organisationalUnitService.getHierarchies(List.of(2L));
        log.debug("hierarchies1 for id=2:"  + hierarchies2);
        assertEquals(2, hierarchies2.get(2L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies3 = organisationalUnitService.getHierarchies(List.of(3L));
        log.debug("hierarchies1 for id=3:"  + hierarchies3);
        assertEquals(3, hierarchies3.get(3L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies4 = organisationalUnitService.getHierarchies(List.of(4L));
        log.debug("hierarchies1 for id=4:"  + hierarchies4);
        assertEquals(4, hierarchies4.get(4L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies5 = organisationalUnitService.getHierarchies(List.of(5L));
        log.debug("hierarchies5 for id=5:"  + hierarchies5);
        assertEquals(2, hierarchies5.get(5L).size());

        Map<Long, List<OrganisationalUnit>> hierarchies6 = organisationalUnitService.getHierarchies(List.of(6L));
        log.debug("hierarchies1 for id=6:"  + hierarchies6);
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
}
