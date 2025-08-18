package uk.gov.cabinetoffice.csl.domain.organisationalunit;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.csrs.Domain;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class OrganisationalUnitTest {
    @Test
    public void testHasDomainReturnsTrueIfSpecifiedDomainExistsInOrganisationalUnit(){
        List<Domain> domains = Arrays.asList(
            new Domain(1L, "domain1.com", LocalDateTime.now()),
            new Domain(2L, "domain2.com", LocalDateTime.now()),
            new Domain(3L, "domain3.com", LocalDateTime.now())
        );

        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setDomains(domains);

        Boolean expectedResult = true;
        Boolean actualResult = organisationalUnit.hasDomain("domain1.com");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testHasDomainReturnsFalseIfSpecifiedDomainDoesNotExistInOrganisationalUnit(){
        List<Domain> domains = Arrays.asList(
                new Domain(1L, "domain1.com", LocalDateTime.now()),
                new Domain(2L, "domain2.com", LocalDateTime.now()),
                new Domain(3L, "domain3.com", LocalDateTime.now())
        );

        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setDomains(domains);

        Boolean expectedResult = true;
        Boolean actualResult = organisationalUnit.hasDomain("domain4.com");

        assertNotEquals(expectedResult, actualResult);
    }
}
