package uk.gov.cabinetoffice.csl.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;
import uk.gov.cabinetoffice.csl.domain.error.GenericServerException;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class HttpClient implements IHttpClient {

    private final RestTemplate restTemplate;

    @Override
    public <R> DownloadableFile executeFileDownloadRequest(RequestEntity<R> request) {
        ResponseEntity<ByteArrayResource> response = executeRawRequest(request, ByteArrayResource.class);
        List<String> contentDisposition = response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION);
        if (contentDisposition == null || contentDisposition.isEmpty()) {
            throw new GenericServerException(
                    String.format("Attempted to download a file which doesn't have the '%s' header", HttpHeaders.CONTENT_DISPOSITION)
            );
        }
        String fileName = contentDisposition.get(0).replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
        return new DownloadableFile(fileName, response.getBody());
    }

    private <T, R> ResponseEntity<T> executeRawRequest(RequestEntity<R> request, Class<T> responseClass) {
        try {
            log.debug("Sending request: {}", request);
            ResponseEntity<T> response = restTemplate.exchange(request, responseClass);
            log.debug("Request response: {}", response);
            return response;
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
        return executeRawRequest(request, responseClass).getBody();
    }

    @Override
    public <T, R> T executeTypeReferenceRequest(RequestEntity<R> request) {
        try {
            log.debug("Sending request: {}", request);
            ResponseEntity<T> response = restTemplate.exchange(request, new ParameterizedTypeReference<>() {
            });
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
}
