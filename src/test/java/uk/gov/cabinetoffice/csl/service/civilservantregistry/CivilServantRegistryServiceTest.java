package uk.gov.cabinetoffice.csl.service.civilservantregistry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.cabinetoffice.csl.client.civilservantregistry.ICivilServantRegistryClient;
import uk.gov.cabinetoffice.csl.domain.csrs.FormattedOrganisationalUnitName;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class CivilServantRegistryServiceTest {

    @Mock
    private ICivilServantRegistryClient civilServantRegistryClient;

    @InjectMocks
    private CivilServantRegistryService civilServantRegistryService;

    @Test
    void shouldReturnFormattedOrganisationalUnitNames() {
        List<OrganisationalUnit> organisationalUnits = createOrgList();
        when(civilServantRegistryClient.getAllOrganisationalUnits()).thenReturn(organisationalUnits);
        List<FormattedOrganisationalUnitName> formattedOrganisationalUnitNames = civilServantRegistryService.getFormattedOrganisationalUnitNames();
        Map<Long, FormattedOrganisationalUnitName> orgMap = formattedOrganisationalUnitNames.stream()
                .collect(Collectors.toMap(FormattedOrganisationalUnitName::getId, o -> o));
        assertEquals("OrgName1", orgMap.get(1L).getFormattedName());
        assertEquals("OrgName1 | OrgName2", orgMap.get(2L).getFormattedName());
        assertEquals("OrgName1 | OrgName2 | OrgName3", orgMap.get(3L).getFormattedName());
        assertEquals("OrgName1 | OrgName2 | OrgName3 | OrgName4", orgMap.get(4L).getFormattedName());
        assertEquals("OrgName1 | OrgName5", orgMap.get(5L).getFormattedName());
        assertEquals("OrgName6", orgMap.get(6L).getFormattedName());
    }

    private List<OrganisationalUnit> createOrgList() {
        List<OrganisationalUnit> organisationalUnits = new ArrayList<>();

        OrganisationalUnit organisationalUnits1 = new OrganisationalUnit();
        organisationalUnits1.setId(1L);
        organisationalUnits1.setName("OrgName1");
        organisationalUnits1.setParentId(null);
        organisationalUnits.add(organisationalUnits1);

        OrganisationalUnit organisationalUnits2 = new OrganisationalUnit();
        organisationalUnits2.setId(2L);
        organisationalUnits2.setName("OrgName2");
        organisationalUnits2.setParentId(1L);
        organisationalUnits.add(organisationalUnits2);

        OrganisationalUnit organisationalUnits3 = new OrganisationalUnit();
        organisationalUnits3.setId(3L);
        organisationalUnits3.setName("OrgName3");
        organisationalUnits3.setParentId(2L);
        organisationalUnits.add(organisationalUnits3);

        OrganisationalUnit organisationalUnits4 = new OrganisationalUnit();
        organisationalUnits4.setId(4L);
        organisationalUnits4.setName("OrgName4");
        organisationalUnits4.setParentId(3L);
        organisationalUnits.add(organisationalUnits4);

        OrganisationalUnit organisationalUnits5 = new OrganisationalUnit();
        organisationalUnits5.setId(5L);
        organisationalUnits5.setName("OrgName5");
        organisationalUnits5.setParentId(1L);
        organisationalUnits.add(organisationalUnits5);

        OrganisationalUnit organisationalUnits6 = new OrganisationalUnit();
        organisationalUnits6.setId(6L);
        organisationalUnits6.setName("OrgName6");
        organisationalUnits.add(organisationalUnits6);

        return organisationalUnits;
    }
}
