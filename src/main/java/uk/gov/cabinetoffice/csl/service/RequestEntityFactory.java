package uk.gov.cabinetoffice.csl.service;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
public class RequestEntityFactory {

    public RequestEntity<?> createGetRequestWithBearerAuth(String strUri, Map<String, String> additionalHeaderParams) {
        URI uri = UriComponentsBuilder.fromUriString(strUri)
                .build()
                .toUri();
        return createGetRequestWithBearerAuth(uri, additionalHeaderParams);
    }

    public RequestEntity<?> createGetRequestWithBearerAuth(URI uri, Map<String, String> additionalHeaderParams) {
        HttpHeaders headers = createHttpHeadersWithBearerAuth(additionalHeaderParams);
        return RequestEntity.get(uri).headers(headers).build();
    }

    public RequestEntity<?> createPostRequestWithBearerAuth(String strUri, Object body,
                                                            Map<String, String> additionalHeaderParams) {
        URI uri = UriComponentsBuilder.fromUriString(strUri)
                .build()
                .toUri();
        return createPostRequestWithBearerAuth(uri, body, additionalHeaderParams);
    }

    public RequestEntity<?> createPostRequestWithBearerAuth(URI uri, Object body,
                                                            Map<String, String> additionalHeaderParams) {
        HttpHeaders headers = createHttpHeadersWithBearerAuth(additionalHeaderParams);
        return RequestEntity.post(uri).headers(headers).body(body);
    }

    public RequestEntity<?> createPatchRequestWithBearerAuth(String strUri, Object body,
                                                            Map<String, String> additionalHeaderParams) {
        URI uri = UriComponentsBuilder.fromUriString(strUri)
                .build()
                .toUri();
        return createPatchRequestWithBearerAuth(uri, body, additionalHeaderParams);
    }

    public RequestEntity<?> createPatchRequestWithBearerAuth(URI uri, Object body,
                                                            Map<String, String> additionalHeaderParams) {
        HttpHeaders headers = createHttpHeadersWithBearerAuth(additionalHeaderParams);
        return RequestEntity.patch(uri).headers(headers).body(body);
    }

    private HttpHeaders createHttpHeadersWithBearerAuth(Map<String, String> additionalHeaderParams) {
        String bearerToken = getBearerTokenFromSecurityContext();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        headers.setContentType(new MediaType("application", "json-patch+json"));
        if(additionalHeaderParams != null && !additionalHeaderParams.isEmpty()) {
            headers.setAll(additionalHeaderParams);
        }
        return headers;
    }

    private String getBearerTokenFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
        return jwtPrincipal.getTokenValue();
    }

    public RequestEntity<?> createGetRequestWithBasicAuth(String strUri, String apiUsername, String apiPassword,
                                                          Map<String, String> additionalHeaderParams) {
        URI uri = UriComponentsBuilder.fromUriString(strUri)
                .build()
                .toUri();
        return createGetRequestWithBasicAuth(uri, apiUsername, apiPassword, additionalHeaderParams);
    }

    public RequestEntity<?> createGetRequestWithBasicAuth(URI uri, String apiUsername, String apiPassword,
                                                          Map<String, String> additionalHeaderParams) {
        HttpHeaders headers = createHttpHeadersWithBasicAuth(apiUsername, apiPassword, additionalHeaderParams);
        return RequestEntity.get(uri).headers(headers).build();
    }

    public RequestEntity<?> createPostRequestWithBasicAuth(String strUri, Object body,
                                                           String apiUsername, String apiPassword,
                                                           Map<String, String> additionalHeaderParams) {
        URI uri = UriComponentsBuilder.fromUriString(strUri)
                .build()
                .toUri();
        return createPostRequestWithBasicAuth(uri, body, apiUsername, apiPassword, additionalHeaderParams);
    }

    public RequestEntity<?> createPostRequestWithBasicAuth(URI uri, Object body,
                                                           String apiUsername, String apiPassword,
                                                           Map<String, String> additionalHeaderParams) {
        HttpHeaders headers = createHttpHeadersWithBasicAuth(apiUsername, apiPassword, additionalHeaderParams);
        return RequestEntity.post(uri).headers(headers).body(body);
    }

    private HttpHeaders createHttpHeadersWithBasicAuth(String apiUsername, String apiPassword,
                                                       Map<String, String> additionalHeaderParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(apiUsername, apiPassword);
        if(additionalHeaderParams != null && !additionalHeaderParams.isEmpty()) {
            headers.setAll(additionalHeaderParams);
        }
        return headers;
    }
}
