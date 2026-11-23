package uk.gov.cabinetoffice.csl.service.messaging;

import java.util.Collection;

public interface IMessagingClient {
    
    void sendMessages(Collection<Object> messageList);
}
