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

    public OrganisationalUnitOverview updateAgencyToken(Long organisationalUnitId, AgencyToken tokenUpdate, boolean create) {
        OrganisationalUnitMap organisationalUnitMap = organisationalUnitMapCache.get();
        organisationalUnitMap.updateAgencyToken(organisationalUnitId, tokenUpdate);
        organisationalUnitMapCache.put(organisationalUnitMap);
        OrganisationalUnit organisationalUnit = organisationalUnitMap.get(organisationalUnitId);
        return organisationalUnitFactory.createOrganisationalUnitOverview(organisationalUnit, create);
    }

    public OrganisationalUnitOverview createAgencyToken(Long organisationalUnitId, AgencyTokenDTO agencyToken) {
        AgencyToken response = civilServantRegistryClient.createAgencyToken(organisationalUnitId, agencyToken);
        return updateAgencyToken(organisationalUnitId, response, true);
    }

    public OrganisationalUnitOverview updateAgencyToken(Long organisationalUnitId, AgencyTokenDTO agencyToken) {
        OrganisationalUnitMap organisationalUnitMap = organisationalUnitMapCache.get();
        AgencyToken existingToken = organisationalUnitMap.getAgencyToken(organisationalUnitId);
        if (!agencyTokenFactory.isCapacityValidForToken(existingToken, agencyToken))
            throw new ValidationException("New token capacity cannot be lower than current spaces used");
        AgencyToken response = civilServantRegistryClient.updateAgencyToken(organisationalUnitId, agencyToken);
        organisationalUnitMap.updateAgencyToken(organisationalUnitId, response);
        OrganisationalUnit organisationalUnit = organisationalUnitMap.get(organisationalUnitId);
        return organisationalUnitFactory.createOrganisationalUnitOverview(organisationalUnit, false);
    }

    public void deleteAgencyToken(Long organisationalUnitId) {
        civilServantRegistryClient.deleteAgencyToken(organisationalUnitId);
        updateAgencyToken(organisationalUnitId, null, false);
    }

}
