package uk.gov.cabinetoffice.csl.service.user;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.messaging.MessageMetadataFactory;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.CompleteProfileMessage;

import java.util.List;

@Service
public class UserProfileService {

    private final MessageMetadataFactory messageMetadataFactory;
    private final UserDetailsService userDetailsService;
    private final IMessagingClient messagingClient;

    public UserProfileService(MessageMetadataFactory messageMetadataFactory, UserDetailsService userDetailsService,
                              IMessagingClient messagingClient) {
        this.messageMetadataFactory = messageMetadataFactory;
        this.userDetailsService = userDetailsService;
        this.messagingClient = messagingClient;
    }

    public void completeProfile(String uid) {
        User user = userDetailsService.getUserWithUid(uid);
        CompleteProfileMessage message = messageMetadataFactory.generateCompleteProfileMessage(user);
        messagingClient.sendMessages(List.of(message));
    }
}
