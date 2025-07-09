package uk.gov.cabinetoffice.csl.service.user;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.messaging.MessageMetadataFactory;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.RegisteredLearnerAccountActivateMessage;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.RegisteredLearnerEmailUpdateMessage;

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
        RegisteredLearnerAccountActivateMessage message = messageMetadataFactory.generateRegisteredLearnerAccountActivateMessage(uid);
        messagingClient.sendMessages(List.of(message));
    }

    public void updateEmail(String uid, String email) {
        RegisteredLearnerEmailUpdateMessage message = messageMetadataFactory.generateRegisteredLearnerEmailUpdateMessage(uid, email);
        messagingClient.sendMessages(List.of(message));
    }
}
