package uk.gov.cabinetoffice.csl.service.messaging.queues;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.service.messaging.model.IMessageMetadata;
import uk.gov.cabinetoffice.csl.service.messaging.model.Message;

@Slf4j
@Service
public class JmsQueueClient implements IQueueClient<IMessageMetadata> {

    private final JmsTemplate jmsTemplate;

    public JmsQueueClient(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void sendMessage(Message<IMessageMetadata> message) {
        log.debug(String.format("Sending message: %s to queue: %s", message, message.getQueue()));
        try {
            jmsTemplate.convertAndSend(message.getQueue(), message);
        } catch (Exception e) {
            log.error("Message {} failed to send to queue {}. Error: {}", message, message.getQueue(), e.toString());
            throw e;
        }
    }

}
