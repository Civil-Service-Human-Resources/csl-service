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

@ExtendWith(MockitoExtension.class)
class AgencyTokenFactoryTest {

    @Mock
    IdentityAPIService identityService;

    @InjectMocks
    private AgencyTokenFactory agencyTokenFactory;

    @Test
    public void testCreateAgencyToken() {
        AgencyTokenDTO agencyTokenDto = new AgencyTokenDTO();
        agencyTokenDto.setToken("token");
        agencyTokenDto.setCapacity(10);
        agencyTokenDto.setDomain(Set.of("domain.com"));
        AgencyToken agencyToken = agencyTokenFactory.createAgencyToken(agencyTokenDto);
        assertEquals("token", agencyToken.getToken());
        assertEquals(0, agencyToken.getCapacityUsed());
        assertEquals(10, agencyToken.getCapacity());
        assertEquals("domain.com", agencyToken.getAgencyDomains().stream().findFirst().get().getDomain());
    }

}
