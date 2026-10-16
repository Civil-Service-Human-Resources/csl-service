package uk.gov.cabinetoffice.csl.service.messaging.queues;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.service.messaging.model.Message;

@Slf4j
@Service
public class JmsQueueClient implements IQueueClient {

    private final JmsTemplate jmsTemplate;
    private final IQueueFailoverClient queueFailoverClient;

    public JmsQueueClient(JmsTemplate jmsTemplate, IQueueFailoverClient queueFailoverClient) {
        this.jmsTemplate = jmsTemplate;
        this.queueFailoverClient = queueFailoverClient;
    }

    @Override
    public void sendMessage(Message message) {
        log.debug("Sending message: {} to queue: {}", message, message.getQueue());
        try {
            jmsTemplate.convertAndSend(message.getQueue(), message);
        } catch (Exception e) {
            log.error("Message {} failed to send to queue {}. Error: {}", message, message.getQueue(), e.toString());
            queueFailoverClient.storeFailedMessage(message);
        }
    }

}
