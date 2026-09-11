package uk.gov.cabinetoffice.csl.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import uk.gov.cabinetoffice.csl.controller.model.ErrorDto;
import uk.gov.cabinetoffice.csl.controller.model.ErrorDtoFactory;
import uk.gov.cabinetoffice.csl.domain.error.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class CSLServiceExceptionHandlerTest {

    private ErrorDtoFactory errorDtoFactory;
    private CSLServiceExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        errorDtoFactory = mock(ErrorDtoFactory.class);
        exceptionHandler = new CSLServiceExceptionHandler(errorDtoFactory);
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
}
