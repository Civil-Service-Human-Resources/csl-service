package uk.gov.cabinetoffice.csl.domain.error;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
