package uk.gov.cabinetoffice.csl.exception;

public class UnknownStatusException extends RuntimeException {
    public UnknownStatusException(String value) {
        super(value);
    }
}
