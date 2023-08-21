package uk.gov.cabinetoffice.csl.client.identity;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.identity.OAuthToken;

import java.util.Collections;

@Service
public class IdentityClient implements IIdentityClient {

    private final IHttpClient client;

    public IdentityClient(@Qualifier("identityHttpClient") IHttpClient client) {
        this.client = client;
    }

    @Override
    public OAuthToken getServiceToken() {
        RequestEntity<Void> request = RequestEntity
                .post("/oauth/token", Collections.singletonMap("grant_type", "client_credentials"))
                .build();
        return client.executeRequest(request, OAuthToken.class);
    }
}
