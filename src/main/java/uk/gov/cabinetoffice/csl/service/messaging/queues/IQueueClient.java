package uk.gov.cabinetoffice.csl.service.messaging.queues;

import uk.gov.cabinetoffice.csl.service.messaging.model.IMessageMetadata;
import uk.gov.cabinetoffice.csl.service.messaging.model.Message;

public interface IQueueClient<T extends IMessageMetadata> {

    void sendMessage(Message<T> message);

}
