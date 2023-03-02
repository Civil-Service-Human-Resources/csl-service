package uk.gov.cabinetoffice.csl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.BearerToken;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.invokeService;

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

    public String getOAuthServiceToken() {
        String accessTokenUrl = oauthTokenUrl + "?grant_type=client_credentials";
        RequestEntity<?> postRequestWithBasicAuth = requestEntityFactory.createPostRequestWithBasicAuth(
                accessTokenUrl, null, clientId, clientSecret, null);
        ResponseEntity<?> tokenResponse = invokeService(postRequestWithBasicAuth);
        if(tokenResponse.getStatusCode().is2xxSuccessful()) {
            BearerToken bearerToken = mapJsonStringToBearerToken((String)tokenResponse.getBody());
            log.debug("bearerToken: {}", bearerToken);
            assert bearerToken != null;
            return bearerToken.getAccessToken();
        }
        return null;
    }

    private BearerToken mapJsonStringToBearerToken(String jsonString) {
        try {
            return new ObjectMapper().readValue(jsonString, BearerToken.class);
        } catch (JsonProcessingException e) {
            log.error("Could not convert the response body into BearerToken object: {}", e.toString());
        }
        return null;
    }
}
