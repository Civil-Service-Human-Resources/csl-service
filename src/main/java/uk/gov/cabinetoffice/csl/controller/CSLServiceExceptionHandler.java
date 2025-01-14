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
import org.webjars.NotFoundException;
import uk.gov.cabinetoffice.csl.controller.model.ErrorDtoFactory;
import uk.gov.cabinetoffice.csl.domain.error.ClientAuthenticationErrorException;
import uk.gov.cabinetoffice.csl.domain.error.GenericServerException;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.error.RecordNotFoundException;

import java.time.Instant;

@ControllerAdvice
@RequiredArgsConstructor
public class CSLServiceExceptionHandler extends ResponseEntityExceptionHandler {

    private final ErrorDtoFactory errorDtoFactory;

    private ProblemDetail createProblemDetail(int statusCode, Exception e, String title) {
        ProblemDetail body = ProblemDetail
                .forStatusAndDetail(HttpStatusCode.valueOf(statusCode), e.getMessage());
        body.setTitle(title);
        body.setProperty("timestamp", Instant.now());
        return body;
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

    @ExceptionHandler(GenericServerException.class)
    public ProblemDetail handleServerException(GenericServerException ex) {
        return createProblemDetail(500, ex, "Server exception");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        return errorDtoFactory.createWithErrorFields(HttpStatus.BAD_REQUEST, result.getFieldErrors()).getAsResponseEntity();
    }

}
