package uk.gov.cabinetoffice.csl.client.identity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.error.InternalAuthErrorException;
import uk.gov.cabinetoffice.csl.domain.identity.OAuthToken;

@Component
@Slf4j
public class IdentityClient implements IIdentityClient {

    private final IHttpClient client;
    @Value("${oauth.tokenUrl}")
    private String token;

    public IdentityClient(@Qualifier("identityHttpClient") IHttpClient client) {
        this.client = client;
    }

    @Override
    public OAuthToken getServiceToken() {
        log.debug("Getting service token from identity service");
        String url = String.format("%s?grant_type=client_credentials", token);
        RequestEntity<Void> request = RequestEntity.post(url).build();
        OAuthToken response = client.executeRequest(request, OAuthToken.class);
        if (response == null) {
            throw new InternalAuthErrorException("Service token response was null");
        }
        return response;
    }
}
