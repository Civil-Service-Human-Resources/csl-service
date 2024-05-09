package uk.gov.cabinetoffice.csl.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@AllArgsConstructor
public class HttpClient implements IHttpClient {

    private final RestTemplate restTemplate;

    private <T, R> T executeRawRequest(RequestEntity<R> request, ParameterizedTypeReference<T> parameterizedTypeReference) {
        try {
            log.debug("Sending request: {}", request);
            ResponseEntity<T> response = restTemplate.exchange(request, parameterizedTypeReference);
            log.debug("Request response: {}", response);
            return response.getBody();
        } catch (RestClientResponseException e) {
            String msg = String.format("Error sending '%s' request to endpoint", request.getMethod());
            if (request.getBody() != null) {
                msg = String.format("%s Body was: %s.", msg, request.getBody().toString());
            }
            msg = String.format("%s Error was: %s", msg, e.getMessage());
            log.error(msg);
            throw e;
        }
    }

    @Override
    public <T, R> T executeRequest(RequestEntity<R> request, Class<T> responseClass) {
        return executeRawRequest(request, new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public <T, R> T executeTypeReferenceRequest(RequestEntity<R> request, ParameterizedTypeReference<T> parameterizedTypeReference) {
        return executeRawRequest(request, parameterizedTypeReference);
    }
}
