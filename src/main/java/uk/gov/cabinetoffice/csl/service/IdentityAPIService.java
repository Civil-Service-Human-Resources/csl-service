package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.identity.IIdentityAPIClient;

import java.util.List;
import java.util.Map;

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

    public Map<String, String> getUidsToEmailsMapWithUids(List<String> uids) {
        return this.identityAPIClient.getUidToEmailMap(uids, List.of());
    }

    public Map<String, String> getUidsToEmailsMapWithEmails(List<String> emails) {
        return this.identityAPIClient.getUidToEmailMap(List.of(), emails);
    }
}
