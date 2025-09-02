package uk.gov.cabinetoffice.csl.domain.csrs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrganisationalUnitMapTest {

    OrganisationalUnitMap organisationalUnitMap;

    @BeforeEach
    public void setUp() {
        organisationalUnitMap = OrganisationalUnitMap.buildFromList(getOrganisationalUnitsWithChildren());
    }

    @Test
    void testGetMultiple() {
        List<Long> result = organisationalUnitMap.getMultiple(Arrays.asList(1L, 2L, 3L), true)
                .stream().map(OrganisationalUnit::getId).collect(Collectors.toList());
        assertEquals(List.of(1L, 4L, 6L, 2L, 3L, 5L), result);
    }

    private List<OrganisationalUnit> getOrganisationalUnitsWithChildren() {
        // Tree:
        // - 1
        //  |_ 4
        //     |_ 6
        // - 2
        // - 3
        //   |_5

        OrganisationalUnit org1 = new OrganisationalUnit();
        org1.setId(1L);

        OrganisationalUnit org2 = new OrganisationalUnit();
        org2.setId(2L);

        OrganisationalUnit org3 = new OrganisationalUnit();
        org3.setId(3L);

        OrganisationalUnit org4 = new OrganisationalUnit();
        org4.setId(4L);

        OrganisationalUnit org5 = new OrganisationalUnit();
        org5.setId(5L);

        OrganisationalUnit org6 = new OrganisationalUnit();
        org6.setId(6L);

        org4.setChildIds(Set.of(6L));
        org1.setChildIds(Set.of(4L));

        org3.setChildIds(Set.of(5L));
        return List.of(org1, org2, org3, org4, org5, org6);
    }
}
