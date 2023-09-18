package uk.gov.cabinetoffice.csl.client.identity;

import uk.gov.cabinetoffice.csl.domain.identity.OAuthToken;

public interface IIdentityClient {

    OAuthToken getServiceToken();
}
