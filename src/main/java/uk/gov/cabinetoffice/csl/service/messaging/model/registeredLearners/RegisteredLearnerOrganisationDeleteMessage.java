package uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners;

import lombok.Getter;

import java.io.Serial;

@Getter
public final class RegisteredLearnerOrganisationDeleteMessage extends RegisteredLearnerMessage<RegisteredLearnerOrganisationDelete> {
    @Serial
    private static final long serialVersionUID = 0L;
    private final RegisteredLearnerOrganisationDelete data;

    public RegisteredLearnerOrganisationDeleteMessage(RegisteredLearnerOrganisationDelete data) {
        super(RegisteredLearnerOperation.DELETE, RegisteredLearnerDataType.ORGANISATION, data);
        this.data = data;
    }
}
