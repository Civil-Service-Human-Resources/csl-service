package uk.gov.cabinetoffice.csl.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.controller.model.ErrorDto;
import uk.gov.cabinetoffice.csl.domain.error.GenericServerException;
import uk.gov.cabinetoffice.csl.domain.error.NotFoundException;
import uk.gov.cabinetoffice.csl.domain.error.ValidationException;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpClientTest {

    private RestTemplate restTemplate;
    private HttpClient httpClient;

    @BeforeEach
    public void setUp() {
        restTemplate = mock(RestTemplate.class);
        httpClient = new HttpClient(restTemplate);
    }

    @Test
    public void testExecuteRequest400WithMatchingBackendErrorResponse() {
        String errorJson = """
                {
                    "timestamp": "2026-09-11T15:11:03.503Z",
                    "errors": [
                        "Hyperlink with title 'Sky news 42' and URL 'https://news.sky.com/uk/42' already exists for Learning tag with name Tag-NJ-2-Name",
                        "Second error message"
                    ],
                    "status": 400,
                    "message": "Validation error"
                }
                """;
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                HttpHeaders.EMPTY,
                errorJson.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        RequestEntity<Void> request = RequestEntity.get(URI.create("http://localhost/test")).build();
        when(restTemplate.exchange(any(RequestEntity.class), eq(String.class))).thenThrow(exception);

        ValidationException thrown = assertThrows(ValidationException.class, () ->
                httpClient.executeRequest(request, String.class)
        );

        assertNotNull(thrown.getErrorDto());
        ErrorDto errorDto = thrown.getErrorDto();
        assertEquals(400, errorDto.getStatus());
        assertEquals("Validation error", errorDto.getMessage());
        assertEquals(List.of(
                "Hyperlink with title 'Sky news 42' and URL 'https://news.sky.com/uk/42' already exists for Learning tag with name Tag-NJ-2-Name",
                "Second error message"
        ), errorDto.getErrors());
    }

    @Test
    public void testExecuteRequest400WithNonMatchingBackendErrorResponse() {
        String errorJson = "{\"error\": \"Something else went wrong\"}";
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                HttpHeaders.EMPTY,
                errorJson.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        RequestEntity<Void> request = RequestEntity.get(URI.create("http://localhost/test")).build();
        when(restTemplate.exchange(any(RequestEntity.class), eq(String.class))).thenThrow(exception);

        ValidationException thrown = assertThrows(ValidationException.class, () ->
                httpClient.executeRequest(request, String.class)
        );

        assertNull(thrown.getErrorDto());
        assertTrue(thrown.getMessage().contains("Status code: 400 BAD_REQUEST"));
    }

    @Test
    public void testExecuteRequest404ThrowsNotFoundException() {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                "".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        RequestEntity<Void> request = RequestEntity.get(URI.create("http://localhost/test")).build();
        when(restTemplate.exchange(any(RequestEntity.class), eq(String.class))).thenThrow(exception);

        assertThrows(NotFoundException.class, () ->
                httpClient.executeRequest(request, String.class)
        );
    }

    @Test
    public void testExecuteRequest500ThrowsGenericServerException() {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                HttpHeaders.EMPTY,
                "".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        RequestEntity<Void> request = RequestEntity.get(URI.create("http://localhost/test")).build();
        when(restTemplate.exchange(any(RequestEntity.class), eq(String.class))).thenThrow(exception);

        assertThrows(GenericServerException.class, () ->
                httpClient.executeRequest(request, String.class)
        );
    }
}
