package uk.gov.cabinetoffice.csl.service.messaging.queues;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import uk.gov.cabinetoffice.csl.service.messaging.model.FailedMessageList;
import uk.gov.cabinetoffice.csl.service.messaging.model.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CacheQueueFailoverClientTest {

    ConcurrentMap<Object, Object> map;
    Cache mockCache;
    CacheQueueFailoverClient failoverClient;

    @BeforeEach
    void setUp() {
        map = new ConcurrentHashMap<>();
        mockCache = new ConcurrentMapCache("test", map, true);
        failoverClient = new CacheQueueFailoverClient(mockCache);
    }

    @Test
    void shouldStoreFailedMessageSuccessfully() {
        Message message = new Message("msg-123", LocalDateTime.now(), "Data", "queue");
        failoverClient.storeFailedMessage(message);
        Message storedMessage = mockCache.get("msg-123", Message.class);

        assertEquals(message, storedMessage);
        FailedMessageList idList = mockCache.get("failedMessageIdList", FailedMessageList.class);
        assertNotNull(idList);
        assertTrue(idList.contains("msg-123"));
    }
    
    @Test
    void shouldGetAllFailedMessagesSuccessfully() {
        Message msg1 = new Message("msg-1", LocalDateTime.now(), "Data", "queue");
        Message msg2 = new Message("msg-2", LocalDateTime.now(), "Data", "queue");

        failoverClient.storeFailedMessage(msg1);
        failoverClient.storeFailedMessage(msg2);

        List<Message> failedMessages = failoverClient.getAllFailedMessages();

        assertEquals(2, failedMessages.size());
        assertEquals(msg1, failedMessages.get(0));
        assertEquals(msg2, failedMessages.get(1));
    }

    @Test
    void shouldClearFailedMessageSuccessfully() {
        Message message = new Message("msg-123", LocalDateTime.now(), "Data", "queue");
        failoverClient.storeFailedMessage(message);
        assertNotNull(mockCache.get("msg-123", Message.class));

        failoverClient.clearFailedMessage("msg-123");

        assertNull(mockCache.get("msg-123", Message.class));
        FailedMessageList idList = mockCache.get("failedMessageIdList", FailedMessageList.class);
        assertNotNull(idList);
        assertTrue(idList.isEmpty());
    }

    @Test
    void shouldHandleExceptionGracefullyWhenStoringFailedMessage() {
        Cache throwingCache = mock(Cache.class);
        when(throwingCache.get(eq("failedMessageIdList"), eq(FailedMessageList.class)))
                .thenThrow(new RuntimeException("Cache connection error"));

        CacheQueueFailoverClient clientWithException = new CacheQueueFailoverClient(throwingCache);
        Message message = new Message("msg-123", LocalDateTime.now(), "Data", "queue");

        clientWithException.storeFailedMessage(message);

        verify(throwingCache, times(1)).get(eq("failedMessageIdList"), eq(FailedMessageList.class));
    }

}
