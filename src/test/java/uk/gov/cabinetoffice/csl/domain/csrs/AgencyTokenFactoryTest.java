package uk.gov.cabinetoffice.csl.domain.csrs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.controller.csrs.model.AgencyTokenDTO;
import uk.gov.cabinetoffice.csl.service.IdentityAPIService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgencyTokenFactoryTest {

    @Mock
    IdentityAPIService identityService;

    @InjectMocks
    private AgencyTokenFactory agencyTokenFactory;

    @Test
    public void testCreateAgencyTokenDTO() {
        AgencyToken agencyToken = new AgencyToken();
        agencyToken.setUid("UID");
        agencyToken.setAgencyDomains(Set.of(new AgencyDomain(1L, "domain.com")));
        AgencyTokenDTO dto = agencyTokenFactory.createAgencyTokenDTO(agencyToken, true);
        assertEquals("UID", dto.getUid());
        assertEquals(0, dto.getCapacityUsed());
        assertEquals("domain.com", dto.getAgencyDomains().toArray()[0].toString());
    }

    @Test
    public void testUpdateAgencyToken() {
        when(identityService.getCapacityUsedForAgencyToken("UID")).thenReturn(10);
        AgencyToken agencyToken = new AgencyToken();
        agencyToken.setUid("UID");
        agencyToken.setAgencyDomains(Set.of(new AgencyDomain(1L, "domain.com")));
        AgencyTokenDTO dto = agencyTokenFactory.createAgencyTokenDTO(agencyToken, false);
        assertEquals("UID", dto.getUid());
        assertEquals(10, dto.getCapacityUsed());
        assertEquals("domain.com", dto.getAgencyDomains().toArray()[0].toString());
    }
}
