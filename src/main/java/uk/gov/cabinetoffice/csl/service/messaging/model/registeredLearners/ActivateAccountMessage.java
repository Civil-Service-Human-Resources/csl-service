package uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners;

import lombok.Getter;

import java.io.Serial;

@Getter
public final class ActivateAccountMessage extends RegisteredLearnerMessage<RegisteredLearnerAccount> {
    @Serial
    private static final long serialVersionUID = 0L;
    private final RegisteredLearnerAccount data;

    public ActivateAccountMessage(RegisteredLearnerAccount data) {
        super(RegisteredLearnerOperation.UPDATE, RegisteredLearnerDataType.ACCOUNT_ACTIVATE, data);
        this.data = data;
    }
}
