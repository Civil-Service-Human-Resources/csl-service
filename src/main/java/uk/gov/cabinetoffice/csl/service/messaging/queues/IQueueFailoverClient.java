package uk.gov.cabinetoffice.csl.service.messaging.queues;

import uk.gov.cabinetoffice.csl.service.messaging.model.Message;

import java.util.List;

public interface IQueueFailoverClient {

    void storeFailedMessage(Message message);

    List<Message> getAllFailedMessages();

    boolean clearFailedMessage(String id);
}
