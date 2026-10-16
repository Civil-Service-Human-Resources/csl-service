package uk.gov.cabinetoffice.csl.service.messaging;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.service.messaging.model.MessageFactory;
import uk.gov.cabinetoffice.csl.service.messaging.queues.JmsQueueClient;

@Service
public class RegisteredLearnersMessagingClient extends DefaultMessagingClient {
    public RegisteredLearnersMessagingClient(JmsQueueClient queueClient, MessageFactory messageFactory) {
        super("registeredLearners", queueClient, messageFactory);
    }
}
