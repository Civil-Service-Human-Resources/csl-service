package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.controller.csrs.model.AgencyTokenDTO;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitOverview;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.domain.error.ValidationException;

@Service
@RequiredArgsConstructor
public class AgencyTokenService {

    private final AgencyTokenFactory agencyTokenFactory;

    private final OrganisationalUnitMapCache organisationalUnitMapCache;
    private final OrganisationalUnitFactory organisationalUnitFactory;
    private final ICSRSClient civilServantRegistryClient;

    public OrganisationalUnit updateAgencyTokenForOrganisation(Long organisationalUnitId, AgencyToken tokenUpdate) {
        OrganisationalUnitMap organisationalUnitMap = organisationalUnitMapCache.get();
        organisationalUnitMap.updateAgencyToken(organisationalUnitId, tokenUpdate);
        organisationalUnitMapCache.put(organisationalUnitMap);
        return organisationalUnitMap.get(organisationalUnitId);
    }

    public OrganisationalUnitOverview createAgencyToken(Long organisationalUnitId, AgencyTokenDTO agencyTokenDto) {
        AgencyToken agencyToken = agencyTokenFactory.createAgencyToken(agencyTokenDto);
        agencyToken = civilServantRegistryClient.createAgencyToken(organisationalUnitId, agencyToken);
        OrganisationalUnit organisationalUnit = updateAgencyTokenForOrganisation(organisationalUnitId, agencyToken);
        return organisationalUnitFactory.createOrganisationalUnitOverview(organisationalUnit, false);
    }

    public OrganisationalUnitOverview updateAgencyToken(Long organisationalUnitId, AgencyTokenDTO agencyTokenDto) {
        OrganisationalUnitMap organisationalUnitMap = organisationalUnitMapCache.get();
        OrganisationalUnit organisationalUnit = organisationalUnitMap.get(organisationalUnitId);
        AgencyToken existingToken = organisationalUnit.getAgencyToken();
        if (!agencyTokenFactory.isCapacityValidForToken(existingToken, agencyTokenDto))
            throw new ValidationException("New token capacity cannot be lower than current spaces used");
        AgencyToken agencyToken = agencyTokenFactory.createAgencyToken(agencyTokenDto);
        agencyToken = civilServantRegistryClient.updateAgencyToken(organisationalUnitId, agencyToken);
        organisationalUnit = organisationalUnitMap.updateAgencyToken(organisationalUnitId, agencyToken);
        return organisationalUnitFactory.createOrganisationalUnitOverview(organisationalUnit, true);
    }

    public void deleteAgencyToken(Long organisationalUnitId) {
        civilServantRegistryClient.deleteAgencyToken(organisationalUnitId);
        updateAgencyTokenForOrganisation(organisationalUnitId, null);
    }

}
