package uk.gov.cabinetoffice.csl.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import uk.gov.cabinetoffice.csl.controller.model.ErrorDto;
import uk.gov.cabinetoffice.csl.domain.error.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class CSLServiceExceptionHandlerTest {

    private CSLServiceExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new CSLServiceExceptionHandler();
    }

    @Test
    public void testHandleValidationExceptionWithErrorDto() {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setStatus(400);
        errorDto.setMessage("Validation error");
        errorDto.setErrors(List.of(
                "Hyperlink with title 'Sky news 42' and URL 'https://news.sky.com/uk/42' already exists for Learning tag with name Tag-NJ-2-Name",
                "Second error message"
        ));

        ValidationException ex = new ValidationException("Error sending request. Status code: 400", errorDto);

        ProblemDetail problemDetail = exceptionHandler.handleValidationException(ex);

        assertEquals(400, problemDetail.getStatus());
        assertEquals("Validation error", problemDetail.getTitle());
        assertEquals("Hyperlink with title 'Sky news 42' and URL 'https://news.sky.com/uk/42' already exists for Learning tag with name Tag-NJ-2-Name. Second error message.", problemDetail.getDetail());
        assertNotNull(problemDetail.getProperties().get("timestamp"));
    }

    @Test
    public void testHandleValidationExceptionWithoutErrorDto() {
        ValidationException ex = new ValidationException("Error sending request. Status code: 400 BAD_REQUEST");

        ProblemDetail problemDetail = exceptionHandler.handleValidationException(ex);

        assertEquals(400, problemDetail.getStatus());
        assertEquals("Validation exception", problemDetail.getTitle());
        assertEquals("Error sending request. Status code: 400 BAD_REQUEST", problemDetail.getDetail());
        assertNotNull(problemDetail.getProperties().get("timestamp"));
    }

    @Test
    public void testHandleValidationExceptionWithIncompleteErrorDto() {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setStatus(400);
        errorDto.setMessage("Validation error");
        errorDto.setErrors(List.of()); // empty errors

        ValidationException ex = new ValidationException("Fallback message", errorDto);

        ProblemDetail problemDetail = exceptionHandler.handleValidationException(ex);

        assertEquals(400, problemDetail.getStatus());
        assertEquals("Validation exception", problemDetail.getTitle());
        assertEquals("Fallback message", problemDetail.getDetail());
    }

    @Test
    public void testHandleMethodArgumentNotValid() throws NoSuchMethodException {
        java.lang.reflect.Method method = CSLServiceExceptionHandlerTest.class.getDeclaredMethod("testHandleMethodArgumentNotValid");
        MethodParameter parameter = new MethodParameter(method, -1);
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "field1", "defaultMessage1"));
        bindingResult.addError(new FieldError("target", "field2", "defaultMessage2"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        HttpHeaders headers = new HttpHeaders();
        WebRequest request = mock(WebRequest.class);

        ResponseEntity<Object> responseEntity = exceptionHandler.handleMethodArgumentNotValid(ex, headers, HttpStatus.BAD_REQUEST, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof ProblemDetail);
        ProblemDetail problemDetail = (ProblemDetail) responseEntity.getBody();
        assertEquals(400, problemDetail.getStatus());
        assertEquals("Validation error", problemDetail.getTitle());
        assertEquals("Field field1 is invalid: defaultMessage1. Field field2 is invalid: defaultMessage2.", problemDetail.getDetail());
        assertNotNull(problemDetail.getProperties().get("timestamp"));
    }
}
