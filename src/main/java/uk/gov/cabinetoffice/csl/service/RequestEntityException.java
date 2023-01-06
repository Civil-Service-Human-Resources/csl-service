package uk.gov.cabinetoffice.csl.service;

public class RequestEntityException extends RuntimeException {
    public RequestEntityException(Throwable cause) {
        super("Unable to create request", cause);
    }
}
