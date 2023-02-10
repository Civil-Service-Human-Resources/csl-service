package uk.gov.cabinetoffice.csl.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
public class RequestEntityFactory {

    public RequestEntity<?> createGetRequestWithBearerAuth(String strUri) {
            URI uri = UriComponentsBuilder.fromUriString(strUri)
                    .build()
                    .toUri();
            return createGetRequestWithBearerAuth(uri);
    }

    public RequestEntity<?> createGetRequestWithBearerAuth(URI uri) {
        HttpHeaders headers = createHttpHeadersWithBearerAuth();
        return RequestEntity.get(uri).headers(headers).build();
    }

    public RequestEntity<?> createPostRequestWithBearerAuth(String strUri, Object body) {
            URI uri = UriComponentsBuilder.fromUriString(strUri)
                    .build()
                    .toUri();
            return createPostRequestWithBearerAuth(uri, body);
    }

    public RequestEntity<?> createPostRequestWithBearerAuth(URI uri, Object body) {
        HttpHeaders headers = createHttpHeadersWithBearerAuth();
        return RequestEntity.post(uri).headers(headers).body(body);
    }

    private HttpHeaders createHttpHeadersWithBearerAuth() {
        String bearerToken = getBearerTokenFromSecurityContext();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        return headers;
    }

    private String getBearerTokenFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
        return jwtPrincipal.getTokenValue();
    }

    public RequestEntity<?> createGetRequestWithBasicAuth(String strUri, String apiUsername, String apiPassword) {
            URI uri = UriComponentsBuilder.fromUriString(strUri)
                    .build()
                    .toUri();
            return createGetRequestWithBasicAuth(uri, apiUsername, apiPassword);
    }

    public RequestEntity<?> createGetRequestWithBasicAuth(URI uri, String apiUsername, String apiPassword) {
        HttpHeaders headers = createHttpHeadersWithBasicAuth(apiUsername, apiPassword);
        return RequestEntity.get(uri).headers(headers).build();
    }

    public RequestEntity<?> createPostRequestWithBasicAuth(String strUri, Object body, String apiUsername, String apiPassword) {
            URI uri = UriComponentsBuilder.fromUriString(strUri)
                    .build()
                    .toUri();
            return createPostRequestWithBasicAuth(uri, body, apiUsername, apiPassword);
    }

    public RequestEntity<?> createPostRequestWithBasicAuth(URI uri, Object body, String apiUsername, String apiPassword) {
        HttpHeaders headers = createHttpHeadersWithBasicAuth(apiUsername, apiPassword);
        return RequestEntity.post(uri).headers(headers).body(body);
    }

    private HttpHeaders createHttpHeadersWithBasicAuth(String apiUsername, String apiPassword) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(apiUsername, apiPassword);
        return headers;
    }
}
