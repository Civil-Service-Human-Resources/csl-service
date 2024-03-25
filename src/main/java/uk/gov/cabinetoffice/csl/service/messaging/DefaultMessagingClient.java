package uk.gov.cabinetoffice.csl.service.messaging;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.service.messaging.model.IMessageMetadata;
import uk.gov.cabinetoffice.csl.service.messaging.model.Message;
import uk.gov.cabinetoffice.csl.service.messaging.model.MessageFactory;
import uk.gov.cabinetoffice.csl.service.messaging.queues.JmsQueueClient;

import java.util.Collection;

@Service
public class DefaultMessagingClient implements IMessagingClient {

    private final JmsQueueClient queueClient;
    private final MessageFactory messageFactory;

    public DefaultMessagingClient(JmsQueueClient queueClient, MessageFactory messageFactory) {
        this.queueClient = queueClient;
        this.messageFactory = messageFactory;
    }
    
    @Override
    public void sendMessages(Collection<IMessageMetadata> messageList) {
        messageList.forEach(message -> {
            Message<IMessageMetadata> fullMessage = messageFactory.generateFullMessage(message);
            queueClient.sendMessage(fullMessage);
        });
    }
}
