package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.controller.csrs.model.AgencyTokenDTO;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitOverview;
import uk.gov.cabinetoffice.csl.domain.csrs.AgencyToken;
import uk.gov.cabinetoffice.csl.domain.csrs.AgencyTokenFactory;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnitFactory;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnitMap;
import uk.gov.cabinetoffice.csl.domain.error.ValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AgencyTokenServiceTest extends CsrsServiceTestBase {

    private OrganisationalUnitMap organisationalUnitMap;

    @Mock
    private AgencyTokenFactory agencyTokenFactory;

    @Mock
    private OrganisationalUnitFactory organisationalUnitFactory;

    @Mock
    OrganisationalUnitMapCache organisationalUnitMapCache;

    @Mock
    private ICSRSClient csrs;

    @InjectMocks
    private AgencyTokenService agencyTokenService;

    @BeforeEach
    public void setUp() {
        organisationalUnitMap = OrganisationalUnitMap.buildFromList(getAllOrganisationalUnits());
        when(organisationalUnitMapCache.get()).thenReturn(organisationalUnitMap);
    }

    @Test
    public void shouldCreateAgencyTokenAndCascadeInheritance() {
        AgencyTokenDTO agencyToken = new AgencyTokenDTO();
        agencyToken.setUid("UID");
        AgencyToken response = new AgencyToken();
        response.setUid("UID");
        when(agencyTokenFactory.createAgencyToken(agencyToken)).thenReturn(response);
        when(csrs.createAgencyToken(1L, response)).thenReturn(response);
        when(organisationalUnitFactory.createOrganisationalUnitOverview(organisationalUnitMap.get(1L), false)).thenReturn(new OrganisationalUnitOverview());
        agencyTokenService.createAgencyToken(1L, agencyToken);
        assertEquals("UID", organisationalUnitMap.get(1L).getAgencyToken().getUid());
        assertEquals("UID", organisationalUnitMap.get(2L).getInheritedAgencyToken().getUid());
    }

    @Test
    public void shouldUpdateAgencyTokenAndCascadeInheritance() {
        AgencyToken existingToken = new AgencyToken();
        existingToken.setUid("UID");
        organisationalUnitMap.get(1L).setAgencyToken(existingToken);
        AgencyTokenDTO agencyToken = new AgencyTokenDTO();
        agencyToken.setUid("UID2");
        AgencyToken response = new AgencyToken();
        response.setUid("UID2");
        when(agencyTokenFactory.createAgencyToken(agencyToken)).thenReturn(response);
        when(agencyTokenFactory.isCapacityValidForToken(existingToken, agencyToken)).thenReturn(true);
        when(csrs.updateAgencyToken(1L, response)).thenReturn(response);
        when(organisationalUnitFactory.createOrganisationalUnitOverview(organisationalUnitMap.get(1L), true)).thenReturn(new OrganisationalUnitOverview());
        agencyTokenService.updateAgencyToken(1L, agencyToken);
        assertEquals("UID2", organisationalUnitMap.get(1L).getAgencyToken().getUid());
        assertEquals("UID2", organisationalUnitMap.get(2L).getInheritedAgencyToken().getUid());
    }

    @Test
    public void shouldValidateTokenSpacesOnUpdate() {
        AgencyToken existingToken = new AgencyToken();
        existingToken.setUid("UID");
        organisationalUnitMap.get(1L).setAgencyToken(existingToken);
        AgencyTokenDTO agencyToken = new AgencyTokenDTO();
        agencyToken.setUid("UID2");
        AgencyToken response = new AgencyToken();
        response.setUid("UID2");
        when(agencyTokenFactory.isCapacityValidForToken(existingToken, agencyToken)).thenReturn(false);
        ValidationException exception = assertThrows(ValidationException.class, () -> agencyTokenService.updateAgencyToken(1L, agencyToken));
        assertEquals("New token capacity cannot be lower than current spaces used", exception.getMessage());
    }

    @Test
    public void shouldDeleteAgencyTokenAndCascadeInheritance() {
        AgencyToken existingToken = new AgencyToken();
        existingToken.setUid("UID");
        organisationalUnitMap.get(1L).setAgencyToken(existingToken);
        organisationalUnitMap.get(2L).setInheritedAgencyToken(existingToken);
        agencyTokenService.deleteAgencyToken(1L);
        assertNull(organisationalUnitMap.get(1L).getAgencyToken());
        assertNull(organisationalUnitMap.get(2L).getInheritedAgencyToken());
    }

}
