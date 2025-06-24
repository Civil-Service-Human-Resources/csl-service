package uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners;


import uk.gov.cabinetoffice.csl.service.messaging.model.IMessageMetadata;

public interface IRegisteredLearnerMessageMetadata extends IMessageMetadata {
    RegisteredLearnerOperation getOperation();

    RegisteredLearnerDataType getDataType();

    <T> T getData();
}
