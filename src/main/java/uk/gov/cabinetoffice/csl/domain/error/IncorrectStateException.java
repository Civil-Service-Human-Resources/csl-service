package uk.gov.cabinetoffice.csl.domain.error;

public class IncorrectStateException extends RuntimeException {
    public IncorrectStateException(String message) {
        super(message);
    }
}
