package uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners;

import lombok.Getter;

import java.io.Serial;

@Getter
public final class RegisteredLearnerOrganisationUpdateMessage extends RegisteredLearnerMessage<RegisteredLearnerOrganisationUpdate> {
    @Serial
    private static final long serialVersionUID = 0L;
    private final RegisteredLearnerOrganisationUpdate data;

    public RegisteredLearnerOrganisationUpdateMessage(RegisteredLearnerOrganisationUpdate data) {
        super(RegisteredLearnerOperation.UPDATE, RegisteredLearnerDataType.ORGANISATION, data);
        this.data = data;
    }
}
