package uk.gov.cabinetoffice.csl.service.user;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.messaging.MessageMetadataFactory;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.ActivateAccountMessage;

import java.util.List;

@Service
public class UserAccountService {

    private final MessageMetadataFactory messageMetadataFactory;
    private final IMessagingClient messagingClient;

    public UserAccountService(MessageMetadataFactory messageMetadataFactory, IMessagingClient messagingClient) {
        this.messageMetadataFactory = messageMetadataFactory;
        this.messagingClient = messagingClient;
    }

    public void activateUser(String uid) {
        ActivateAccountMessage message = messageMetadataFactory.generateActivateAccountMessage(uid);
        messagingClient.sendMessages(List.of(message));
    }
}
