package uk.gov.cabinetoffice.csl.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;
import uk.gov.cabinetoffice.csl.client.model.PagedResponse;
import uk.gov.cabinetoffice.csl.controller.model.ErrorDto;
import uk.gov.cabinetoffice.csl.domain.error.GenericServerException;
import uk.gov.cabinetoffice.csl.domain.error.NotFoundException;
import uk.gov.cabinetoffice.csl.domain.error.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Getter
public class HttpClient implements IHttpClient {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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
    
    @Override
    public <T, R extends PagedResponse<T>> List<T> getPaginatedRequest(Class<R> pagedResponseClass, UriComponentsBuilder url, Integer maxPageSize) {
        List<T> results = new ArrayList<>();
        int totalPages = 1;
        url.queryParam("size", maxPageSize).queryParam("page", 0);
        for (int i = 0; i < totalPages; i++) {
            RequestEntity<Void> request = RequestEntity.get(url.build().toUriString()).build();
            R response = executeRequest(request, pagedResponseClass);
            results.addAll(response.getContent());
            totalPages = response.getTotalPages();
            url.replaceQueryParam("page", i + 1);
        }
        return results;
    }

    private <T, R> ResponseEntity<T> executeRawRequest(RequestEntity<R> request, Class<T> responseClass) {
        try {
            log.debug("Sending request: {}", request);
            ResponseEntity<T> response = restTemplate.exchange(request, responseClass);
            log.debug("Request response: {}", response);
            return response;
        } catch (RestClientResponseException e) {
            throw handleRestClientException(e);
        }
    }

    private RuntimeException handleRestClientException(RestClientResponseException e) {
        String msg = String.format("Error sending request. Status code: %s", e.getStatusCode());
        if (!e.getResponseBodyAsString().isEmpty()) {
            msg = String.format("%s Body was: %s.", msg, e.getResponseBodyAsString());
        }
        msg = String.format("%s Error was: %s", msg, e.getMessage());
        log.error(msg);
        if (e.getStatusCode().equals(HttpStatusCode.valueOf(404))) {
            throw new NotFoundException(msg);
        } else if (e.getStatusCode().equals(HttpStatusCode.valueOf(400))) {
            ErrorDto errorDto = parseErrorDto(e.getResponseBodyAsString());
            throw new ValidationException(msg, errorDto);
        }
        throw new GenericServerException(msg);
    }

    private ErrorDto parseErrorDto(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }
        try {
            ErrorDto errorDto = OBJECT_MAPPER.readValue(responseBody, ErrorDto.class);
            if (errorDto.getMessage() != null && !errorDto.getMessage().isBlank()
                    && errorDto.getErrors() != null && !errorDto.getErrors().isEmpty()
                    && errorDto.getStatus() != 0) {
                return errorDto;
            }
        } catch (Exception ex) {
            log.debug("Could not parse error response body as ErrorDto: {}", ex.getMessage());
        }
        return null;
    }

    @Override
    public <T, R> T executeRequest(RequestEntity<R> request, Class<T> responseClass) {
        return executeRawRequest(request, responseClass).getBody();
    }

    @Override
    public <T, R> T executeTypeReferenceRequest(RequestEntity<R> request, ParameterizedTypeReference<T> parameterizedTypeReference) {
        try {
            log.debug("Sending request: {}", request);
            ResponseEntity<T> response = restTemplate.exchange(request, parameterizedTypeReference);
            log.debug("Request response: {}", response);
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw handleRestClientException(e);
        }

    }

    public <T, R> Map<String, T> executeMapRequest(RequestEntity<R> request, ParameterizedTypeReference<Map<String, T>> ptr) {
        return executeTypeReferenceRequest(request, ptr);
    }
}
