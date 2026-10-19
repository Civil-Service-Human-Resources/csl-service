package uk.gov.cabinetoffice.csl.service.messaging.queues;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.service.messaging.model.FailedMessageList;
import uk.gov.cabinetoffice.csl.service.messaging.model.Message;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
public class CacheQueueFailoverClient implements IQueueFailoverClient {

    private final Cache cache;
    private final String LIST_ID = "failedMessageIdList";

    public CacheQueueFailoverClient(@Qualifier("messagingQueueFailover") Cache cache) {
        this.cache = cache;
    }

    private FailedMessageList getFailedMessageList() {
        return Optional.ofNullable(cache.get(LIST_ID, FailedMessageList.class))
                .orElse(new FailedMessageList());
    }

    private void storeFailedMessageID(String messageId) {
        FailedMessageList idList = getFailedMessageList();
        log.info("Storing failed message ID in cache");
        if (idList.contains(messageId)) {
            log.info("Message with ID {} already exists in cache", messageId);
            return;
        }
        idList.add(messageId);
        cache.put(LIST_ID, idList);
    }

    private void deleteFailedMessageID(String messageId) {
        FailedMessageList idList = getFailedMessageList();
        log.info("Deleting message ID in cache");
        idList.remove(messageId);
        cache.put(LIST_ID, idList);
    }

    @Override
    public void storeFailedMessage(Message message) {
        try {
            storeFailedMessageID(message.getMessageId());
            log.info("Storing failed message in cache");
            cache.put(message.getMessageId(), message);
            log.info("Failed message stored successfully");
        } catch (Exception e) {
            log.error("FATAL: Failed to store failed message in cache. Message: {}. Exception: {}", message, e.toString());
        }
    }

    private List<Message> fetchMessageStream(Stream<String> stream) {
        List<Message> messages = stream.map(id -> cache.get(id, Message.class)).toList();
        log.info("{} messages found", messages.size());
        return messages;
    }

    @Override
    public List<Message> getAllFailedMessages() {
        log.info("Fetching all messages from the messaging failover queue");
        return fetchMessageStream(getFailedMessageList().stream());
    }

    @Override
    public boolean clearFailedMessage(String id) {
        log.info("Evicting {} from the messaging failover cache", id);
        try {
            deleteFailedMessageID(id);
            cache.evict(id);
        } catch (Exception e) {
            log.error("FATAL: Failed to remove failed message from cache. MessageID: {}. Exception: {}", id, e.toString());
            return false;
        }
        return true;
    }
}
