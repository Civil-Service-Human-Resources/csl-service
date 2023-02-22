package uk.gov.cabinetoffice.csl.service;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Component
public class RequestEntityWithBearerAuthFactory {

    public RequestEntity<?> createGetRequestWithBearerAuth(String strUri, Map<String, String> additionalHeaderParams) {
        URI uri = UriComponentsBuilder.fromUriString(strUri)
                .build()
                .toUri();
        return createGetRequestWithBearerAuth(uri, additionalHeaderParams);
    }

    public RequestEntity<?> createGetRequestWithBearerAuth(URI uri, Map<String, String> additionalHeaderParams) {
        HttpHeaders headers = createHttpHeadersWithBearerAuth(additionalHeaderParams, MediaType.APPLICATION_JSON);
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
        HttpHeaders headers = createHttpHeadersWithBearerAuth(additionalHeaderParams, MediaType.APPLICATION_JSON);
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
        HttpHeaders headers = createHttpHeadersWithBearerAuth(additionalHeaderParams,
                new MediaType("application", "json-patch+json"));
        return RequestEntity.patch(uri).headers(headers).body(body);
    }

    private HttpHeaders createHttpHeadersWithBearerAuth(Map<String, String> additionalHeaderParams,
                                                        MediaType mediaType) {
        String bearerToken = getBearerTokenFromSecurityContext();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        headers.setContentType(mediaType);
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
}
