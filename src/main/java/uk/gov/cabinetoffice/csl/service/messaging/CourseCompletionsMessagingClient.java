package uk.gov.cabinetoffice.csl.service.messaging;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.service.messaging.model.MessageFactory;
import uk.gov.cabinetoffice.csl.service.messaging.queues.JmsQueueClient;

@Service
public class CourseCompletionsMessagingClient extends DefaultMessagingClient {
    public CourseCompletionsMessagingClient(JmsQueueClient queueClient, MessageFactory messageFactory) {
        super("coursecompletions", queueClient, messageFactory);
    }
}
