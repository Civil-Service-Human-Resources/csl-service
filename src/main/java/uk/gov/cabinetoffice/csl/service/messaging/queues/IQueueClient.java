package uk.gov.cabinetoffice.csl.service.messaging.queues;

import uk.gov.cabinetoffice.csl.service.messaging.model.Message;

public interface IQueueClient {

    void sendMessage(Message message);
}
