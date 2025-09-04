package uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners;

import lombok.Getter;

import java.io.Serial;

@Getter
public final class RegisteredLearnersOrganisationDeleteMessage extends RegisteredLearnerMessage<RegisteredLearnersOrganisationDelete> {
    @Serial
    private static final long serialVersionUID = 0L;
    private final RegisteredLearnersOrganisationDelete data;

    public RegisteredLearnersOrganisationDeleteMessage(RegisteredLearnersOrganisationDelete data) {
        super(RegisteredLearnerOperation.DELETE, RegisteredLearnerDataType.ORGANISATION, data);
        this.data = data;
    }
}
