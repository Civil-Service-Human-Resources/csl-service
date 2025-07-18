package uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners;

import lombok.Getter;

import java.io.Serial;

@Getter
public final class RegisteredLearnerEmailUpdateMessage extends RegisteredLearnerMessage<RegisteredLearnerEmailUpdate> {
    @Serial
    private static final long serialVersionUID = 0L;
    private final RegisteredLearnerEmailUpdate data;

    public RegisteredLearnerEmailUpdateMessage(RegisteredLearnerEmailUpdate data) {
        super(RegisteredLearnerOperation.UPDATE, RegisteredLearnerDataType.EMAIL_UPDATE, data);
        this.data = data;
    }
}
