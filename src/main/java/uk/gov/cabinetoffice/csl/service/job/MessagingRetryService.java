package uk.gov.cabinetoffice.csl.service.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.service.job.model.FailedMessagesResult;
import uk.gov.cabinetoffice.csl.service.messaging.model.Message;
import uk.gov.cabinetoffice.csl.service.messaging.queues.IQueueFailoverClient;
import uk.gov.cabinetoffice.csl.service.messaging.queues.JmsQueueClient;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class MessagingRetryService {

    private final Integer maxMessageBatchSize;
    private final IQueueFailoverClient client;
    private final JmsQueueClient jmsClient;

    public MessagingRetryService(@Value("${messaging.config.failover.maxMessageBatchSize:}") @Nullable Integer maxMessageBatchSize,
                                 IQueueFailoverClient client,
                                 JmsQueueClient jmsClient) {
        this.maxMessageBatchSize = maxMessageBatchSize;
        this.client = client;
        this.jmsClient = jmsClient;
    }

    public FailedMessagesResult processFailedMessages() {
        List<Message> messages = client.getAllFailedMessages();
        if (messages.isEmpty()) {
            log.info("No failed messages found");
            return new FailedMessagesResult(0, 0);
        }
        List<Message> messagesToProcess = maxMessageBatchSize != null ? messages.subList(0, maxMessageBatchSize) : messages;
        log.info("Processing {} failed messages", messages.size());
        return processFailedMessages(messagesToProcess);
    }

    private FailedMessagesResult processFailedMessages(Collection<Message> messages) {
        Integer processed = messages.stream().map(m -> {
            try {
                jmsClient.sendMessage(m);
                if (client.clearFailedMessage(m.getMessageId())) {
                    return 1;
                }
                return 0;
            } catch (Exception e) {
                log.error("Exception when attempting to re-send message. Not clearing from queue.");
                return 0;
            }
        }).reduce(0, Integer::sum);
        return new FailedMessagesResult(processed, messages.size());
    }

    public Integer getFailedMessagesCount() {
        return client.getAllFailedMessages().size();
    }
}
