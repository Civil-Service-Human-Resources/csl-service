package uk.gov.cabinetoffice.csl.service.csrs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.controller.model.FormattedOrganisationalUnitsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.Domain;
import uk.gov.cabinetoffice.csl.domain.csrs.FormattedOrganisationalUnitName;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnitMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganisationalUnitListServiceTest {

    @Mock
    private ICSRSClient csrs;

    @InjectMocks
    private OrganisationalUnitListService organisationalUnitListService;

    @BeforeEach
    public void setUp() {
        OrganisationalUnitMap map = OrganisationalUnitMap.buildFromList(getAllOrgs());
        when(csrs.getAllOrganisationalUnits()).thenReturn(map);
    }

    @Test
    public void shouldReturnFormattedOrganisationalUnitNamesForAllOrganisationsIfParametersAreNotSpecified() {
        FormattedOrganisationalUnitsParams formattedOrganisationalUnitsParams = new FormattedOrganisationalUnitsParams();
        formattedOrganisationalUnitsParams.setOrganisationId(null);
        formattedOrganisationalUnitsParams.setDomain(null);

        Map<Long, FormattedOrganisationalUnitName> orgMap = organisationalUnitListService.getFormattedOrganisationalUnitNames(formattedOrganisationalUnitsParams)
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
        FormattedOrganisationalUnitsParams formattedOrganisationalUnitsParams = new FormattedOrganisationalUnitsParams();
        formattedOrganisationalUnitsParams.setOrganisationId(Arrays.asList(1L, 2L, 3L));
        formattedOrganisationalUnitsParams.setDomain("domain1.com");

        Map<Long, FormattedOrganisationalUnitName> orgMap = organisationalUnitListService.getFormattedOrganisationalUnitNames(formattedOrganisationalUnitsParams)
                .getFormattedOrganisationalUnitNames().stream().collect(Collectors.toMap(FormattedOrganisationalUnitName::getId, Function.identity()));

        assertEquals(orgMap.size(), 4);
        assertEquals("OrgName1 (OName1)", orgMap.get(1L).getName());
        assertEquals("OrgName1 (OName1) | OrgName2", orgMap.get(2L).getName());
        assertEquals("OrgName1 (OName1) | OrgName2 | OrgName3 (OName3)", orgMap.get(3L).getName());
        assertEquals("OrgName1 (OName1) | OrgName5 (OName5)", orgMap.get(5L).getName());
    }

    private List<OrganisationalUnit> getAllOrgs() {

        Domain domain1 = new Domain(1L, "domain1.com", LocalDateTime.now());
        Domain domain2 = new Domain(2L, "domain2.com", LocalDateTime.now());
        Domain domain3 = new Domain(3L, "domain3.com", LocalDateTime.now());

        List<OrganisationalUnit> organisationalUnits = new ArrayList<>();

        OrganisationalUnit organisationalUnits1 = new OrganisationalUnit();
        organisationalUnits1.setId(1L);
        organisationalUnits1.setName("OrgName1");
        organisationalUnits1.setFormattedName("OrgName1 (OName1)");
        organisationalUnits1.setParentId(null);
        organisationalUnits1.setAbbreviation("OName1");
        organisationalUnits1.setCode("ON1");
        organisationalUnits1.setDomains(Arrays.asList(domain1, domain2));
        organisationalUnits.add(organisationalUnits1);

        OrganisationalUnit organisationalUnits2 = new OrganisationalUnit();
        organisationalUnits2.setId(2L);
        organisationalUnits2.setName("OrgName2");
        organisationalUnits2.setFormattedName("OrgName1 (OName1) | OrgName2");
        organisationalUnits2.setParentId(1L);
        organisationalUnits2.setAbbreviation("");
        organisationalUnits2.setCode("ON2");
        organisationalUnits2.setDomains(Arrays.asList(domain1, domain2, domain3));
        organisationalUnits.add(organisationalUnits2);

        OrganisationalUnit organisationalUnits3 = new OrganisationalUnit();
        organisationalUnits3.setId(3L);
        organisationalUnits3.setName("OrgName3");
        organisationalUnits3.setFormattedName("OrgName1 (OName1) | OrgName2 | OrgName3 (OName3)");
        organisationalUnits3.setParentId(2L);
        organisationalUnits3.setAbbreviation("OName3");
        organisationalUnits3.setCode("ON3");
        organisationalUnits.add(organisationalUnits3);

        OrganisationalUnit organisationalUnits4 = new OrganisationalUnit();
        organisationalUnits4.setId(4L);
        organisationalUnits4.setName("OrgName4");
        organisationalUnits4.setFormattedName("OrgName1 (OName1) | OrgName2 | OrgName3 (OName3) | OrgName4 (OName4)");
        organisationalUnits4.setParentId(3L);
        organisationalUnits4.setAbbreviation("OName4");
        organisationalUnits4.setCode("ON4");
        organisationalUnits.add(organisationalUnits4);

        OrganisationalUnit organisationalUnits5 = new OrganisationalUnit();
        organisationalUnits5.setId(5L);
        organisationalUnits5.setName("OrgName5");
        organisationalUnits5.setFormattedName("OrgName1 (OName1) | OrgName5 (OName5)");
        organisationalUnits5.setParentId(1L);
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
        organisationalUnits6.setDomains(Arrays.asList(domain3));
        organisationalUnits.add(organisationalUnits6);

        return organisationalUnits;
    }

}
