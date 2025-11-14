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

    public AgencyToken formatAgencyToken(AgencyToken existingToken) {
        int capacityUsed = identityService.getCapacityUsedForAgencyToken(existingToken.getUid());
        existingToken.setCapacityUsed(capacityUsed);
        return existingToken;
    }

    public AgencyToken createAgencyToken(AgencyTokenDTO agencyTokenDTO) {
        return new AgencyToken(agencyTokenDTO.getToken(), agencyTokenDTO.getCapacity(),
                agencyTokenDTO.getDomain().stream().map(AgencyDomain::new).collect(Collectors.toSet()));
    }

}
