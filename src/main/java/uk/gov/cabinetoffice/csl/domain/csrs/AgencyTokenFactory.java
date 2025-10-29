package uk.gov.cabinetoffice.csl.domain.csrs;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.csrs.model.AgencyTokenDTO;
import uk.gov.cabinetoffice.csl.service.IdentityAPIService;

import java.util.stream.Collectors;

@Service
public class AgencyTokenFactory {

    private final IdentityAPIService identityService;

    public AgencyTokenFactory(IdentityAPIService identityService) {
        this.identityService = identityService;
    }

    public boolean isCapacityValidForToken(AgencyToken existingToken, AgencyTokenDTO newToken) {
        if (newToken.getCapacity() < existingToken.getCapacity()) {
            return newToken.getCapacity() > identityService.getCapacityUsedForAgencyToken(existingToken.getUid());
        } else {
            return true;
        }
    }

    public AgencyTokenDTO createAgencyTokenDTO(AgencyToken agencyToken, boolean newToken) {
        int capacityUsed = newToken ? 0 : identityService.getCapacityUsedForAgencyToken(agencyToken.getUid());
        return new AgencyTokenDTO(agencyToken.getUid(),
                agencyToken.getAgencyDomains().stream().map(AgencyDomain::getDomain).collect(Collectors.toSet()),
                agencyToken.getCapacity(), capacityUsed, agencyToken.getToken());
    }

}
