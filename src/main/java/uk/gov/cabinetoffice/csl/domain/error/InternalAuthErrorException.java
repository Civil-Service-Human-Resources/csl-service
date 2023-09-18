package uk.gov.cabinetoffice.csl.domain.error;

public class InternalAuthErrorException extends GenericServerException {
    public InternalAuthErrorException(String message) {
        super(message);
    }
}
