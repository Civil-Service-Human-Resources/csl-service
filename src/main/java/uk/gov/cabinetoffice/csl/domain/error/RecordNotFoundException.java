package uk.gov.cabinetoffice.csl.domain.error;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String message) {
        super(message);
    }
}
