package uk.gov.cabinetoffice.csl.service.messaging;

import uk.gov.cabinetoffice.csl.service.messaging.model.Message;
import uk.gov.cabinetoffice.csl.service.messaging.model.MessageFactory;
import uk.gov.cabinetoffice.csl.service.messaging.queues.JmsQueueClient;

import java.util.Collection;

public class DefaultMessagingClient implements IMessagingClient {

    private final String queue;
    private final JmsQueueClient queueClient;
    private final MessageFactory messageFactory;

    public DefaultMessagingClient(String queue, JmsQueueClient queueClient, MessageFactory messageFactory) {
        this.queue = queue;
        this.queueClient = queueClient;
        this.messageFactory = messageFactory;
    }

    @Override
    public void sendMessages(Collection<Object> messageList) {
        messageList.forEach(message -> {
            Message fullMessage = messageFactory.generateFullMessage(queue, message);
            queueClient.sendMessage(fullMessage);
        });
    }
}
