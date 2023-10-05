package uk.gov.cabinetoffice.csl.domain.error;

public class ClientAuthenticationErrorException extends RuntimeException {
    public ClientAuthenticationErrorException(String message) {
        super(message);
    }
}
