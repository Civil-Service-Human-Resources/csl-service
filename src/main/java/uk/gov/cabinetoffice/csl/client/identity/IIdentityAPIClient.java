package uk.gov.cabinetoffice.csl.client.identity;

import uk.gov.cabinetoffice.csl.domain.identity.AgencyTokenCapacityUsed;

public interface IIdentityAPIClient {
    AgencyTokenCapacityUsed getCapacityUsedForAgencyToken(String tokenUid);
}
