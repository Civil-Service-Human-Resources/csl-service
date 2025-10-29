package uk.gov.cabinetoffice.csl.domain.csrs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrganisationalUnitMapTest {

    TestDataService testDataService = new TestDataService();

    private OrganisationalUnitMap organisationalUnitMap;

    @BeforeEach
    public void setup() {
        organisationalUnitMap = OrganisationalUnitMap.create(testDataService.generateOrganisationalUnitsPagedResponse().getContent());
    }

    @Test
    void testGetOrganisationalUnitTree() {
        List<BasicOrganisationalUnitNode> tree = organisationalUnitMap.getOrganisationalUnitTree();
        assertEquals("OrgName1", tree.get(0).getName());
        assertEquals("OrgName2", tree.get(0).getChildren().get(0).getName());
        assertEquals("OrgName3", tree.get(0).getChildren().get(0).getChildren().get(0).getName());
        assertEquals("OrgName4", tree.get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getName());
        assertEquals("OrgName5", tree.get(0).getChildren().get(1).getName());
        assertEquals("OrgName6", tree.get(1).getName());
        assertEquals("OrgName7", tree.get(2).getName());
    }

    @Test
    void testDeleteOrganisationalUnits() {
        List<Long> deletedIds = organisationalUnitMap.delete(1L);
        assertEquals(5, deletedIds.size());
        assertEquals(1L, deletedIds.get(0));
        assertEquals(2L, deletedIds.get(1));
        assertEquals(3L, deletedIds.get(2));
        assertEquals(4L, deletedIds.get(3));
        assertEquals(5L, deletedIds.get(4));
    }

    @Test
    void testGetHierarchy() {
        List<OrganisationalUnit> hierarchy = organisationalUnitMap.getHierarchy(3L);
        assertEquals(3, hierarchy.size());
        assertEquals(3L, hierarchy.get(0).getId());
        assertEquals(2L, hierarchy.get(1).getId());
        assertEquals(1L, hierarchy.get(2).getId());
    }

    @Test
    void testGetHierarchies() {
        Map<Long, List<OrganisationalUnit>> hierarchy = organisationalUnitMap.getHierarchies(List.of(3L, 5L));
        assertEquals(2, hierarchy.size());
        assertEquals(3, hierarchy.get(3L).size());
        assertEquals(3L, hierarchy.get(3L).get(0).getId());
        assertEquals(2L, hierarchy.get(3L).get(1).getId());
        assertEquals(1L, hierarchy.get(3L).get(2).getId());
        assertEquals(2, hierarchy.get(5L).size());
        assertEquals(5L, hierarchy.get(5L).get(0).getId());
        assertEquals(1L, hierarchy.get(5L).get(1).getId());
    }

    @Test
    void testAddAgencyToken() {
        // OrgName7 has an agency token
        // OrgName8 is a child of OrgName7 but does not have its own token, so will inherit it from 7
        // OrgName9 is a child of OrgName7 and has its own token
        AgencyToken newToken = new AgencyToken();
        newToken.setUid("NEW_TOKEN");
        organisationalUnitMap.updateAgencyToken(7L, newToken);
        List<OrganisationalUnit> result = organisationalUnitMap.getMultiple(List.of(7L), true);

        assertEquals("NEW_TOKEN", result.get(0).getAgencyToken().getUid());
        assertNull(result.get(0).getInheritedAgencyToken());

        assertEquals("NEW_TOKEN", result.get(1).getInheritedAgencyToken().getUid());
        assertNull(result.get(1).getAgencyToken());

        assertEquals("uid2", result.get(2).getAgencyToken().getUid());
        assertNull(result.get(2).getInheritedAgencyToken());
    }
}
