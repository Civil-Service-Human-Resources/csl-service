package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.identity.IIdentityAPIClient;

@Slf4j
@Service
public class IdentityAPIService {

    private final IIdentityAPIClient identityAPIClient;

    public IdentityAPIService(IIdentityAPIClient identityAPIClient) {
        this.identityAPIClient = identityAPIClient;
    }

    public Integer getCapacityUsedForAgencyToken(String uid) {
        return identityAPIClient.getCapacityUsedForAgencyToken(uid).getCapacityUsed();
    }
}
