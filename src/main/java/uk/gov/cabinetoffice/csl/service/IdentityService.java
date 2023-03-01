package uk.gov.cabinetoffice.csl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.domain.BearerToken;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.returnError;

@Slf4j
@Service
public class IdentityService {

    private final RequestEntityWithBasicAuthFactory requestEntityFactory;

    private final String oauthTokenUrl;

    private final String clientId;

    private final String clientSecret;

    private final RestTemplate restTemplate;

    public IdentityService(RequestEntityWithBasicAuthFactory requestEntityFactory,
                           RestTemplate restTemplate,
                           @Value("${oauth.tokenUrl}") String oauthTokenUrl,
                           @Value("${oauth.clientId}") String clientId,
                           @Value("${oauth.clientSecret}") String clientSecret) {
        this.requestEntityFactory = requestEntityFactory;
        this.restTemplate = restTemplate;
        this.oauthTokenUrl = oauthTokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public BearerToken getOAuthServiceToken() throws Exception {
        String accessTokenUrl = oauthTokenUrl + "?grant_type=client_credentials";
        RequestEntity<?> postRequestWithBasicAuth = requestEntityFactory.createPostRequestWithBasicAuth(
                accessTokenUrl, null, clientId, clientSecret, null);
        ResponseEntity<?> tokenResponse = getToken(postRequestWithBasicAuth);
        if(tokenResponse.getStatusCode().is2xxSuccessful()) {
            BearerToken bearerToken = new ObjectMapper().readValue((String)tokenResponse.getBody(), BearerToken.class);
            log.debug("bearerToken: {}", bearerToken);
            return bearerToken;
        }
        return null;
    }

    private ResponseEntity<?> getToken(RequestEntity<?> postRequestWithBasicAuth) {
        ResponseEntity<?> response;
        try {
            response = restTemplate.exchange(postRequestWithBasicAuth, String.class);
        } catch (HttpStatusCodeException ex) {
            response = returnError(ex, postRequestWithBasicAuth.getUrl().getPath());
        }
        return response;
    }
}
