package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class RequestEntityFactory {
    @Value("${oauth.clientId}")
    private String clientId;

    @Value("${oauth.clientSecret}")
    private String clientSecret;

    @Value("${oauth.tokenUrl}")
    private String clientUrl;

    public RequestEntity<?> createGetRequest(String strUri) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(strUri)
                    .build()
                    .toUri();
            return createGetRequest(uri);
        } catch (JSONException e) {
            throw new RequestEntityException(e);
        }
    }

    public RequestEntity<?> createGetRequest(URI uri) throws JSONException {
        HttpHeaders headers = createHttpHeaders();
        return RequestEntity.get(uri).headers(headers).build();
    }

    public RequestEntity<?> createPostRequest(String strUri, Object body) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(strUri)
                    .build()
                    .toUri();
            return createPostRequest(uri, body);
        } catch (JSONException e) {
            throw new RequestEntityException(e);
        }
    }

    public RequestEntity<?> createPostRequest(URI uri, Object body) throws JSONException {
        HttpHeaders headers = createHttpHeaders();
        return RequestEntity.post(uri).headers(headers).body(body);
    }

    private HttpHeaders createHttpHeaders() throws JSONException {
        String clientAccessToken = getClientAccessTokenFromSecurityContext();
        if(StringUtils.isBlank(clientAccessToken)) {
            clientAccessToken = getClientAccessTokenFromCache();
            if(StringUtils.isBlank(clientAccessToken)) {
                clientAccessToken = getClientAccessTokenFromIdentityService();
            }
        }
        log.debug("Token Value: {}", clientAccessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + clientAccessToken);
        return headers;
    }

    private String getClientAccessTokenFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
        log.debug("Authenticated?: {}", authentication.isAuthenticated());
        log.debug("Authentication jwtPrincipal: {}", jwtPrincipal);
        log.debug("Authentication jwtPrincipal Claims: {}", jwtPrincipal.getClaims());
        log.debug("Authentication jwtPrincipal Headers: {}",  jwtPrincipal.getHeaders());
        log.debug("Authentication jwtPrincipal ExpiresAt: {}", jwtPrincipal.getExpiresAt());
        log.debug("Authentication jwtPrincipal Id: {}", jwtPrincipal.getId());
        log.debug("Authentication jwtPrincipal IssuedAt: {}", jwtPrincipal.getIssuedAt());
        log.debug("Authentication jwtPrincipal TokenValue: {}", jwtPrincipal.getTokenValue());
        String tokenValue = jwtPrincipal.getTokenValue();
        //TODO: check if token is client type and not expired then return it else return null
        //isTokenExpired(tokenValue);
        return tokenValue;
    }

    private String getClientAccessTokenFromCache() {
        //TODO: Retrieve token from cache and check if token is not expired then return it else return null
        //isTokenExpired(tokenValue);
        return null;
    }

    private String getClientAccessTokenFromIdentityService() throws JSONException {
        RestTemplate restTemplate = new RestTemplate();

        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes(), false));

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Basic " + encodedCredentials);

        HttpEntity<String> request = new HttpEntity<String>(headers);

        String access_token_url = clientUrl;
        access_token_url += "?grant_type=client_credentials";

        ResponseEntity<String> response = restTemplate.exchange(access_token_url, HttpMethod.POST, request, String.class);
        JSONObject jsonObject = new JSONObject(response.getBody());
        String tokenValue = jsonObject.getString("access_token");
        cacheClientAccessToken(tokenValue);
        //TODO: Write code to decrypt the tokenValue for debugging
        return tokenValue;
    }

    private void cacheClientAccessToken(String tokenValue) {
        //TODO: Implement this
    }

    private boolean isTokenExpired(String expiryDate) {
        //TODO: Implement this
        return false;
    }
}
