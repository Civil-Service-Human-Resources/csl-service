package uk.gov.cabinetoffice.csl.factory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
public class RequestEntityWithBasicAuthFactory {

    public RequestEntity<?> createGetRequestWithBasicAuth(String strUri, String apiUsername, String apiPassword,
                                                          Map<String, String> additionalHeaderParams) {
        URI uri = UriComponentsBuilder.fromUriString(strUri).build().toUri();
        return createGetRequestWithBasicAuth(uri, apiUsername, apiPassword, additionalHeaderParams);
    }

    public RequestEntity<?> createGetRequestWithBasicAuth(URI uri, String apiUsername, String apiPassword,
                                                          Map<String, String> additionalHeaderParams) {
        HttpHeaders headers = createHttpHeadersWithBasicAuth(apiUsername, apiPassword, additionalHeaderParams,
                MediaType.APPLICATION_JSON);
        return RequestEntity.get(uri).headers(headers).build();
    }

    public RequestEntity<?> createPostRequestWithBasicAuth(String strUri, Object body,
                                                           String apiUsername, String apiPassword,
                                                           Map<String, String> additionalHeaderParams) {
        URI uri = UriComponentsBuilder.fromUriString(strUri).build().toUri();
        return createPostRequestWithBasicAuth(uri, body, apiUsername, apiPassword, additionalHeaderParams);
    }

    public RequestEntity<?> createPostRequestWithBasicAuth(URI uri, Object body,
                                                           String apiUsername, String apiPassword,
                                                           Map<String, String> additionalHeaderParams) {
        HttpHeaders headers = createHttpHeadersWithBasicAuth(apiUsername, apiPassword, additionalHeaderParams,
                MediaType.APPLICATION_JSON);
        if(body != null) {
            return RequestEntity.post(uri).headers(headers).body(body);
        } else {
            return RequestEntity.post(uri).headers(headers).build();
        }
    }

    public HttpHeaders createHttpHeadersWithBasicAuth(String apiUsername, String apiPassword,
                                                      Map<String, String> additionalHeaderParams, MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(mediaType));
        headers.setContentType(mediaType);
        headers.setBasicAuth(apiUsername, apiPassword);
        if(additionalHeaderParams != null && !additionalHeaderParams.isEmpty()) {
            headers.setAll(additionalHeaderParams);
        }
        return headers;
    }
}
