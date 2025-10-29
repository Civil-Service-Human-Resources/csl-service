package uk.gov.cabinetoffice.csl.domain.csrs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OrganisationalUnitFactoryTest {

    @InjectMocks
    private OrganisationalUnitFactory organisationalUnitFactory;

    private OrganisationalUnit generateOrganisationalUnit(Long id, String name, Long parentId) {
        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setId(id);
        organisationalUnit.setName(name);
        organisationalUnit.setParentId(parentId);
        return organisationalUnit;
    }

    @Test
    public void testBuildOrgMap() {
        List<OrganisationalUnit> orgs = List.of(
                generateOrganisationalUnit(1L, "org1", null),
                generateOrganisationalUnit(2L, "org2", 1L),
                generateOrganisationalUnit(3L, "org3", 1L),
                generateOrganisationalUnit(4L, "org4", 2L),
                generateOrganisationalUnit(5L, "org5", 6L),
                generateOrganisationalUnit(6L, "org6", null)
        );
        OrganisationalUnitMap map = organisationalUnitFactory.buildOrganisationalUnits(orgs);
        assertEquals(6, map.size());
        assertEquals("org1", map.get(1L).getFormattedName());
        assertEquals(Set.of(2L, 3L), map.get(1L).getChildIds());

        assertEquals("org1 | org2", map.get(2L).getFormattedName());
        assertEquals(Set.of(4L), map.get(2L).getChildIds());

        assertEquals("org1 | org3", map.get(3L).getFormattedName());
        assertEquals(Set.of(), map.get(3L).getChildIds());

        assertEquals("org1 | org2 | org4", map.get(4L).getFormattedName());
        assertEquals(Set.of(), map.get(4L).getChildIds());

        assertEquals("org6 | org5", map.get(5L).getFormattedName());
        assertEquals(Set.of(), map.get(5L).getChildIds());

        assertEquals("org6", map.get(6L).getFormattedName());
        assertEquals(Set.of(5L), map.get(6L).getChildIds());

    }

}
