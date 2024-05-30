package uk.gov.cabinetoffice.csl.domain.error;

public class RecordNotFoundException extends NotFoundException {
    public RecordNotFoundException(String message) {
        super(message);
    }
}
