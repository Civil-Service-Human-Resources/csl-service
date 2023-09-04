package uk.gov.cabinetoffice.csl.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.cabinetoffice.csl.domain.error.ClientAuthenticationErrorException;
import uk.gov.cabinetoffice.csl.domain.error.GenericServerException;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.error.RecordNotFoundException;

import java.time.Instant;

@ControllerAdvice
public class CSLServiceExceptionHandler extends ResponseEntityExceptionHandler {

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
}
