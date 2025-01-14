package uk.gov.cabinetoffice.csl.domain.error;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
