package uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners;

import lombok.Getter;

import java.io.Serial;
import java.util.List;

@Getter
public final class RegisteredLearnerOrganisationUpdateMessage extends RegisteredLearnerMessage<List<RegisteredLearnerOrganisationUpdate>> {
    @Serial
    private static final long serialVersionUID = 0L;
    private final List<RegisteredLearnerOrganisationUpdate> data;

    public RegisteredLearnerOrganisationUpdateMessage(List<RegisteredLearnerOrganisationUpdate> data) {
        super(RegisteredLearnerOperation.UPDATE, RegisteredLearnerDataType.ORGANISATION, data);
        this.data = data;
    }
}
