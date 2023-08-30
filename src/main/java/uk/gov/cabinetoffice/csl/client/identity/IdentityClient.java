package uk.gov.cabinetoffice.csl.client.identity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.identity.GrantRequest;
import uk.gov.cabinetoffice.csl.domain.identity.OAuthToken;

@Component
@Slf4j
public class IdentityClient implements IIdentityClient {

    private final IHttpClient client;

    public IdentityClient(@Qualifier("identityHttpClient") IHttpClient client) {
        this.client = client;
    }

    @Override
    public OAuthToken getServiceToken() {
        log.debug("Getting service token from identity service");
        GrantRequest body = new GrantRequest("client_credentials");
        RequestEntity<GrantRequest> request = RequestEntity
                .post("/oauth/token").body(body);
        return client.executeRequest(request, OAuthToken.class);
    }
}
