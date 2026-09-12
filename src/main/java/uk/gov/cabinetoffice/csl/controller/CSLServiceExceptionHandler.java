package uk.gov.cabinetoffice.csl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.cabinetoffice.csl.controller.model.ErrorDto;
import uk.gov.cabinetoffice.csl.domain.error.*;

import java.time.Instant;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
@RequiredArgsConstructor
public class CSLServiceExceptionHandler extends ResponseEntityExceptionHandler {

    private ProblemDetail createProblemDetail(int statusCode, String detail, String title) {
        ProblemDetail body = ProblemDetail
                .forStatusAndDetail(HttpStatusCode.valueOf(statusCode), detail);
        body.setTitle(title);
        body.setProperty("timestamp", Instant.now());
        return body;
    }

    private ProblemDetail createProblemDetail(int statusCode, Exception e, String title) {
        return createProblemDetail(statusCode, e.getMessage(), title);
    }

    @ExceptionHandler(IncorrectStateException.class)
    public ProblemDetail handleIncorrectStateException(IncorrectStateException ex) {
        return createProblemDetail(400, ex, "Record is in the incorrect state");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        return createProblemDetail(403, ex, "Access is denied");
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleRecordNotFoundException(NotFoundException ex) {
        return createProblemDetail(404, ex, "Resource Not Found");
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ProblemDetail handleRecordNotFoundException(RecordNotFoundException ex) {
        return createProblemDetail(404, ex, "Record Not Found");
    }

    @ExceptionHandler(ClientAuthenticationErrorException.class)
    public ProblemDetail handleAuthException(ClientAuthenticationErrorException ex) {
        return createProblemDetail(400, ex, "Client authentication exception");
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidationException(ValidationException ex) {
        ErrorDto errorDto = ex.getErrorDto();
        if (errorDto != null && errorDto.getMessage() != null && !errorDto.getMessage().isBlank()
                && errorDto.getErrors() != null && !errorDto.getErrors().isEmpty()
                && errorDto.getStatus() != 0) {
            String detail = String.join(". ", errorDto.getErrors()) + ".";
            return createProblemDetail(errorDto.getStatus(), detail, errorDto.getMessage());
        }
        return createProblemDetail(400, ex, "Validation exception");
    }

    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleForbiddenException(ForbiddenException ex) {
        return createProblemDetail(403, ex, "Forbidden exception");
    }

    @ExceptionHandler(GenericServerException.class)
    public ProblemDetail handleServerException(GenericServerException ex) {
        return createProblemDetail(500, ex, "Server exception");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<String> errors = result.getFieldErrors().stream()
                .map(ef -> String.format("Field %s is invalid: %s", ef.getField(), ef.getDefaultMessage()))
                .toList();
        String detail = String.join(". ", errors) + ".";
        return new ResponseEntity<>(createProblemDetail(BAD_REQUEST.value(), detail, "Validation error"), BAD_REQUEST);
    }

}
