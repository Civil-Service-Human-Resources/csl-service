package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.identity.OAuthToken;
import uk.gov.cabinetoffice.csl.factory.RequestEntityWithBasicAuthFactory;

import java.time.LocalDateTime;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.invokeService;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.mapJsonStringToObject;

@Slf4j
@Service
public class IdentityService {

    private final RequestEntityWithBasicAuthFactory requestEntityFactory;

    @Value("${oauth.tokenUrl}")
    private String oauthTokenUrl;

    @Value("${oauth.clientId}")
    private String clientId;

    @Value("${oauth.clientSecret}")
    private String clientSecret;

    public IdentityService(RequestEntityWithBasicAuthFactory requestEntityFactory) {
        this.requestEntityFactory = requestEntityFactory;
    }

    public ResponseEntity<?> getOAuthServiceToken() {
        log.info("IdentityService.getOAuthServiceToken: Invoking identity service to retrieve service token.");
        String accessTokenUrl = oauthTokenUrl + "?grant_type=client_credentials";
        RequestEntity<?> postRequestWithBasicAuth = requestEntityFactory.createPostRequestWithBasicAuth(
                accessTokenUrl, null, clientId, clientSecret, null);
        return invokeService(postRequestWithBasicAuth);
    }

    @Cacheable("service-token")
    public OAuthToken getCachedOAuthServiceToken() {
        OAuthToken oAuthToken = new OAuthToken();
        ResponseEntity<?> tokenResponse = getOAuthServiceToken();
        if(tokenResponse.getStatusCode().is2xxSuccessful()) {
            oAuthToken = mapJsonStringToObject((String) tokenResponse.getBody(), OAuthToken.class);
            assert oAuthToken != null;
            oAuthToken.setExpiryDateTime(LocalDateTime.now().plusSeconds(oAuthToken.getExpiresIn()));
        } else {
            log.error("Unable to retrieve service token from identity-service. " +
                    "Following response is received from identity-service: {}", tokenResponse);
        }
        return oAuthToken;
    }

    @CacheEvict(value = "service-token", allEntries = true)
    public void removeServiceTokenFromCache() {
        log.info("IdentityService.removeServiceTokenFromCache: service token is removed from the cache.");
    }
}
