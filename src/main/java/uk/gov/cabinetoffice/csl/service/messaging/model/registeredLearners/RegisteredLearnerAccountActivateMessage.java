package uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners;

import lombok.Getter;

import java.io.Serial;

@Getter
public final class RegisteredLearnerAccountActivateMessage extends RegisteredLearnerMessage<RegisteredLearnerAccountActivate> {
    @Serial
    private static final long serialVersionUID = 0L;
    private final RegisteredLearnerAccountActivate data;

    public RegisteredLearnerAccountActivateMessage(RegisteredLearnerAccountActivate data) {
        super(RegisteredLearnerOperation.UPDATE, RegisteredLearnerDataType.ACCOUNT_ACTIVATE, data);
        this.data = data;
    }
}
