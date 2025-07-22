package uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners;

import lombok.Getter;

import java.io.Serial;

@Getter
public final class UpdateProfileMessage extends RegisteredLearnerMessage<RegisteredLearnerProfile> {
    @Serial
    private static final long serialVersionUID = 0L;
    private final RegisteredLearnerProfile data;

    public UpdateProfileMessage(RegisteredLearnerProfile data) {
        super(RegisteredLearnerOperation.UPDATE, RegisteredLearnerDataType.LEARNER_PROFILE, data);
        this.data = data;
    }
}
