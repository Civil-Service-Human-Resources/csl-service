package uk.gov.cabinetoffice.csl.domain.error;

import lombok.Getter;
import uk.gov.cabinetoffice.csl.controller.model.ErrorDto;

@Getter
public class ValidationException extends RuntimeException {
    private final ErrorDto errorDto;

    public ValidationException(String message) {
        super(message);
        this.errorDto = null;
    }

    public ValidationException(String message, ErrorDto errorDto) {
        super(message);
        this.errorDto = errorDto;
    }
}
