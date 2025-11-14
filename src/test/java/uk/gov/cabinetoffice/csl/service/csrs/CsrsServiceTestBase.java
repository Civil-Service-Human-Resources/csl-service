package uk.gov.cabinetoffice.csl.service.csrs;

import uk.gov.cabinetoffice.csl.domain.csrs.Domain;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsrsServiceTestBase {

    protected List<OrganisationalUnit> getAllOrganisationalUnits() {

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
        organisationalUnits6.setDomains(List.of(domain3));
        organisationalUnits.add(organisationalUnits6);

        return organisationalUnits;
    }

}
