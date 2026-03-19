package uk.gov.cabinetoffice.csl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.cabinetoffice.csl.controller.model.ErrorDtoFactory;
import uk.gov.cabinetoffice.csl.domain.error.*;

import java.time.Instant;

@ControllerAdvice
@RequiredArgsConstructor
public class CSLServiceExceptionHandler {

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

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidationException(ValidationException ex) {
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

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException ex) {
        BindingResult result = ex.getBindingResult();
        ProblemDetail problem = createProblemDetail(400, ex, "Validation exception");
        result.getFieldErrors().forEach(fe -> problem.setProperty(fe.getField(), fe.getDefaultMessage() == null ? "Unknown error" : fe.getDefaultMessage()));
        return ResponseEntity.of(problem).build();
    }

}
