package uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor
@Data
public class RegisteredLearnerMessage<T> implements IRegisteredLearnerMessageMetadata {
    @Serial
    private static final long serialVersionUID = 0L;
    private final RegisteredLearnerOperation operation;
    private final RegisteredLearnerDataType dataType;
    private final T data;

    @Override
    public String getQueue() {
        return "registeredlearners";
    }
}
