package uk.gov.cabinetoffice.csl.domain.error;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
