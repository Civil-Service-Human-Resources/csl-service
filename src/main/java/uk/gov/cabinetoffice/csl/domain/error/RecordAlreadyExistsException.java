package uk.gov.cabinetoffice.csl.domain.error;

public class RecordAlreadyExistsException extends RuntimeException {
    public RecordAlreadyExistsException(String message) {
        super(message);
    }
}
