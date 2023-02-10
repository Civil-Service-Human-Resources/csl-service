package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class RequestEntityFactory {

    public RequestEntity<?> createGetRequestWithBearerToken(String strUri) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(strUri)
                    .build()
                    .toUri();
            return createGetRequestWithBearerToken(uri);
        } catch (JSONException e) {
            throw new RequestEntityException(e);
        }
    }

    public RequestEntity<?> createGetRequestWithBearerToken(URI uri) throws JSONException {
        HttpHeaders headers = createHttpHeadersWithBearerToken();
        return RequestEntity.get(uri).headers(headers).build();
    }

    public RequestEntity<?> createPostRequestWithBearerToken(String strUri, Object body) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(strUri)
                    .build()
                    .toUri();
            return createPostRequestWithBearerToken(uri, body);
        } catch (JSONException e) {
            throw new RequestEntityException(e);
        }
    }

    public RequestEntity<?> createPostRequestWithBearerToken(URI uri, Object body) throws JSONException {
        HttpHeaders headers = createHttpHeadersWithBearerToken();
        return RequestEntity.post(uri).headers(headers).body(body);
    }

    private HttpHeaders createHttpHeadersWithBearerToken() throws JSONException {
        String bearerToken = getBearerTokenFromSecurityContext();
        log.debug("Bearer Token Value: {}", bearerToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        return headers;
    }

    private String getBearerTokenFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
        return jwtPrincipal.getTokenValue();
    }

    public RequestEntity<?> createGetRequestWithCredentials(String strUri, String apiUsername, String apiPassword) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(strUri)
                    .build()
                    .toUri();
            return createGetRequestWithCredentials(uri, apiUsername, apiPassword);
        } catch (JSONException e) {
            throw new RequestEntityException(e);
        }
    }

    public RequestEntity<?> createGetRequestWithCredentials(URI uri, String apiUsername, String apiPassword) throws JSONException {
        HttpHeaders headers = createHttpHeadersWithCredentials(apiUsername, apiPassword);
        return RequestEntity.get(uri).headers(headers).build();
    }

    public RequestEntity<?> createPostRequestWithCredentials(String strUri, Object body, String apiUsername, String apiPassword) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(strUri)
                    .build()
                    .toUri();
            return createPostRequestWithCredentials(uri, body, apiUsername, apiPassword);
        } catch (JSONException e) {
            throw new RequestEntityException(e);
        }
    }

    public RequestEntity<?> createPostRequestWithCredentials(URI uri, Object body, String apiUsername, String apiPassword) throws JSONException {
        HttpHeaders headers = createHttpHeadersWithCredentials(apiUsername, apiPassword);
        return RequestEntity.post(uri).headers(headers).body(body);
    }

    private HttpHeaders createHttpHeadersWithCredentials(String apiUsername, String apiPassword) throws JSONException {
        String apiCredentials = apiUsername + ":" + apiPassword;
        String encodedApiCredentials = new String(Base64.encodeBase64(apiCredentials.getBytes(), false));

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Basic " + encodedApiCredentials);
        return headers;
    }
}
