package uk.gov.cabinetoffice.csl.domain.error;

public class GenericServerException extends RuntimeException {
    public GenericServerException(String message) {
        super(message);
    }
}
