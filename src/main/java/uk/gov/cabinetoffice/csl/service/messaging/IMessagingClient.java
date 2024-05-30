package uk.gov.cabinetoffice.csl.service.messaging;

import uk.gov.cabinetoffice.csl.service.messaging.model.IMessageMetadata;

import java.util.Collection;

public interface IMessagingClient {

    void sendMessages(Collection<IMessageMetadata> messageList);
}
