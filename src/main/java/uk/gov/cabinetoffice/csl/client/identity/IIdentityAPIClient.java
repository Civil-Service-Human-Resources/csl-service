package uk.gov.cabinetoffice.csl.client.identity;

import uk.gov.cabinetoffice.csl.domain.identity.AgencyTokenCapacityUsed;
import uk.gov.cabinetoffice.csl.domain.identity.Identity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IIdentityAPIClient {
    AgencyTokenCapacityUsed getCapacityUsedForAgencyToken(String tokenUid);

    Optional<Identity> getIdentityWithEmail(String email);

    Map<String, Identity> fetchByUids(List<String> uids);
}
