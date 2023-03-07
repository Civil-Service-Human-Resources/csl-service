package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.OAuthToken;

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

    @Cacheable("service-token")
    public OAuthToken getOAuthServiceToken() {
        log.info("IdentityService.getOAuthServiceToken: Invoking identity service to retrieve service token.");
        OAuthToken oAuthToken = new OAuthToken();
        String accessTokenUrl = oauthTokenUrl + "?grant_type=client_credentials";
        RequestEntity<?> postRequestWithBasicAuth = requestEntityFactory.createPostRequestWithBasicAuth(
                accessTokenUrl, null, clientId, clientSecret, null);
        ResponseEntity<?> tokenResponse = invokeService(postRequestWithBasicAuth);
        if(tokenResponse.getStatusCode().is2xxSuccessful()) {
            oAuthToken = mapJsonStringToObject((String) tokenResponse.getBody(), OAuthToken.class);
            assert oAuthToken != null;
            oAuthToken.setExpiryDate(LocalDateTime.now().plusSeconds(oAuthToken.getExpiresIn()));
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
